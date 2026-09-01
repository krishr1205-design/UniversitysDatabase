package com.example.program1.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.program1.ui.theme.AppThemeMode
import org.json.JSONArray
import org.json.JSONObject

private const val PREFS_NAME = "student_directory"
private const val KEY_STUDENTS = "students"
private const val KEY_UNIVERSITIES = "universities"
private const val KEY_GROUPS = "groups"
private const val KEY_DELETED_IDS = "deleted_student_ids"
private const val KEY_APP_THEME = "app_theme"

// ─── Students ────────────────────────────────────────────────────────────────

fun saveAddedStudents(context: Context, students: List<Student>) {
    val array = JSONArray()
    students.forEach { student ->
        val obj = JSONObject().apply {
            put("id", student.id)
            put("name", student.name)
            put("college", student.college)
            put("phone", student.phone)
            put("group", student.group)
            put("imageUri", student.imageUri ?: "")
            put(
                "imageResourceName",
                if (student.imageResource != null) {
                    context.resources.getResourceEntryName(student.imageResource)
                } else {
                    ""
                }
            )
            put("color", student.color.value.toLong())
        }
        array.put(obj)
    }
    prefs(context).edit().putString(KEY_STUDENTS, array.toString()).apply()
}

fun loadSavedStudents(context: Context): List<Student> {
    val saved = prefs(context).getString(KEY_STUDENTS, null) ?: return emptyList()
    return try {
        val array = JSONArray(saved)
        (0 until array.length()).mapNotNull { i ->
            val obj = array.getJSONObject(i)
            val imageResourceName = obj.optString("imageResourceName").trim()
            val imageResource = imageResourceName
                .takeIf { it.isNotEmpty() }
                ?.let { name ->
                    context.resources
                        .getIdentifier(name, "drawable", context.packageName)
                        .takeIf { it != 0 }
                }
            Student(
                id = obj.getString("id"),
                name = obj.getString("name"),
                college = obj.getString("college"),
                phone = obj.getString("phone"),
                group = obj.getString("group"),
                color = Color(obj.getLong("color").toULong()),
                imageResource = imageResource,
                imageUri = obj.getString("imageUri").ifEmpty { null }
            )
        }
    } catch (_: Exception) {
        emptyList()
    }
}

// ─── Universities ─────────────────────────────────────────────────────────────

fun hasSavedUniversities(context: Context): Boolean =
    prefs(context).contains(KEY_UNIVERSITIES)

fun saveUniversities(context: Context, universities: List<String>) {
    val array = JSONArray()
    universities.map { it.trim() }.filter { it.isNotEmpty() }.distinct().forEach { array.put(it) }
    prefs(context).edit().putString(KEY_UNIVERSITIES, array.toString()).apply()
}

fun loadSavedUniversities(context: Context): List<String> {
    val saved = prefs(context).getString(KEY_UNIVERSITIES, null) ?: return emptyList()
    return try {
        val array = JSONArray(saved)
        (0 until array.length())
            .mapNotNull { i ->
                when (val item = array.get(i)) {
                    is JSONObject -> item.optString("name").trim().ifEmpty { null }
                    else -> {
                        val raw = item.toString().trim()
                        if (raw.startsWith("{") && raw.endsWith("}")) {
                            runCatching { JSONObject(raw).optString("name").trim().ifEmpty { null } }
                                .getOrNull()
                        } else {
                            raw.ifEmpty { null }
                        }
                    }
                }
            }
            .distinct()
    } catch (_: Exception) {
        emptyList()
    }
}

// ─── Groups ───────────────────────────────────────────────────────────────────

fun hasSavedGroups(context: Context): Boolean =
    prefs(context).contains(KEY_GROUPS)

fun saveGroups(context: Context, groups: List<GroupInfo>) {
    val array = JSONArray()
    groups.distinctBy { "${it.university}::${it.name}" }.forEach { group ->
        val obj = JSONObject().apply {
            put("university", group.university)
            put("name", group.name)
        }
        array.put(obj)
    }
    prefs(context).edit().putString(KEY_GROUPS, array.toString()).apply()
}

fun loadSavedGroups(context: Context): List<GroupInfo> {
    val saved = prefs(context).getString(KEY_GROUPS, null) ?: return emptyList()
    return try {
        val array = JSONArray(saved)
        (0 until array.length())
            .mapNotNull { i ->
                when (val item = array.get(i)) {
                    is JSONObject -> {
                        val university = item.optString("university").trim()
                        val name = item.optString("name").trim()
                        if (university.isNotEmpty() && name.isNotEmpty()) {
                            GroupInfo(university = university, name = name)
                        } else null
                    }
                    else -> {
                        val name = item.toString().trim()
                        if (name.isNotEmpty()) GroupInfo(university = "KL University", name = name)
                        else null
                    }
                }
            }
            .distinctBy { "${it.university}::${it.name}" }
    } catch (_: Exception) {
        emptyList()
    }
}

// ─── Deleted Student IDs ──────────────────────────────────────────────────────

fun saveDeletedStudentIds(context: Context, ids: Set<String>) {
    val array = JSONArray()
    ids.sorted().forEach { array.put(it) }
    prefs(context).edit().putString(KEY_DELETED_IDS, array.toString()).apply()
}

fun loadDeletedStudentIds(context: Context): List<String> {
    val saved = prefs(context).getString(KEY_DELETED_IDS, null) ?: return emptyList()
    return try {
        val array = JSONArray(saved)
        (0 until array.length()).mapNotNull { i -> array.optString(i).trim().ifEmpty { null } }.distinct()
    } catch (_: Exception) {
        emptyList()
    }
}

// ─── App Theme ────────────────────────────────────────────────────────────────

fun saveAppTheme(context: Context, mode: AppThemeMode) {
    prefs(context).edit().putString(KEY_APP_THEME, mode.name).apply()
}

fun loadAppTheme(context: Context): AppThemeMode {
    val saved = prefs(context).getString(KEY_APP_THEME, AppThemeMode.OCEAN.name)
        ?: AppThemeMode.OCEAN.name
    return runCatching { AppThemeMode.valueOf(saved) }
        .getOrDefault(AppThemeMode.OCEAN)
}

// ─── Private helpers ──────────────────────────────────────────────────────────

private fun prefs(context: Context) =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
