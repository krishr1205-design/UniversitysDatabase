package com.example.program1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.program1.data.*
import com.example.program1.ui.screens.*
import com.example.program1.ui.theme.*
import com.example.program1.ui.theme.Program1Theme

/** Navigation state machine — each value maps to a distinct screen. */
sealed class Screen {
    object Welcome : Screen()
    object Captcha : Screen()
    object Universities : Screen()
    object Groups : Screen()
    object StudentList : Screen()
    object AddStudent : Screen()
    object EditStudent : Screen()
    object StudentDetails : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Program1Theme {
                StudentApp()
            }
        }
    }
}

@Composable
fun StudentApp() {
    val context = LocalContext.current

    // ── Navigation ────────────────────────────────────────────────────────────
    var screen by remember { mutableStateOf<Screen>(Screen.Welcome) }

    // ── CAPTCHA ───────────────────────────────────────────────────────────────
    var captchaCode by remember { mutableStateOf(generateCaptcha()) }
    var captchaInput by remember { mutableStateOf("") }
    var captchaError by remember { mutableStateOf(false) }

    // ── Selection State ───────────────────────────────────────────────────────
    var selectedUniversity by remember { mutableStateOf("KL University") }
    var selectedGroup by remember { mutableStateOf("Anime Group") }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    var editingStudent by remember { mutableStateOf<Student?>(null) }

    // ── Form State ────────────────────────────────────────────────────────────
    var newName by remember { mutableStateOf("") }
    var newId by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newPhotoUri by remember { mutableStateOf<String?>(null) }
    var addError by remember { mutableStateOf("") }
    var editError by remember { mutableStateOf("") }

    // ── Theme ─────────────────────────────────────────────────────────────────
    var appThemeMode by remember { mutableStateOf(loadAppTheme(context)) }
    var showThemeDialog by remember { mutableStateOf(false) }

    // ── Seeded default data ───────────────────────────────────────────────────
    val defaultUniversity = "KL University"
    val defaultGroupNames = remember { listOf("Anime Group", "Cartoon Group", "Rangers Group") }

    val storedUniversities = remember { loadSavedUniversities(context) }
    var universities by remember {
        mutableStateOf(
            if (hasSavedUniversities(context)) storedUniversities else listOf(defaultUniversity)
        )
    }

    LaunchedEffect(Unit) {
        if (storedUniversities.isNotEmpty()) saveUniversities(context, storedUniversities)
    }

    val storedGroups = remember { loadSavedGroups(context) }
    var groups by remember {
        mutableStateOf(
            if (hasSavedGroups(context)) storedGroups
            else defaultGroupNames.map { GroupInfo(university = defaultUniversity, name = it) }
        )
    }

    var deletedStudentIds by remember {
        mutableStateOf(loadDeletedStudentIds(context).toSet())
    }

    val defaultStudents = remember { buildDefaultStudents() }

    var students by remember {
        mutableStateOf(
            (loadSavedStudents(context) + defaultStudents)
                .distinctBy { it.id }
                .filter { it.id !in deletedStudentIds }
        )
    }

    // ── Photo picker ──────────────────────────────────────────────────────────
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) { }
            newPhotoUri = uri.toString()
        }
    }

    // ── Theme wrapper ─────────────────────────────────────────────────────────
    val appColors = appThemeColors(appThemeMode)

    MaterialTheme(colorScheme = appMaterialColors(appThemeMode)) {
        androidx.compose.runtime.CompositionLocalProvider(
            LocalAppTheme provides appColors,
            androidx.compose.material3.LocalContentColor provides appColors.text
        ) {
            Surface(modifier = Modifier.fillMaxSize(), color = appColors.background) {

                when (screen) {

                    Screen.Welcome -> WelcomeScreen(
                        onStart = {
                            captchaCode = generateCaptcha()
                            captchaInput = ""
                            captchaError = false
                            screen = Screen.Captcha
                        },
                        onTheme = { showThemeDialog = true },
                        themeName = appThemeMode.title
                    )

                    Screen.Captcha -> CaptchaScreen(
                        captchaCode = captchaCode,
                        captchaInput = captchaInput,
                        captchaError = captchaError,
                        onInputChange = { captchaInput = it; captchaError = false },
                        onRefresh = {
                            captchaCode = generateCaptcha(); captchaInput = ""; captchaError = false
                        },
                        onVerify = {
                            if (captchaInput.trim().equals(captchaCode, ignoreCase = true)) {
                                captchaInput = ""; captchaError = false; screen = Screen.Universities
                            } else {
                                captchaError = true; captchaCode = generateCaptcha(); captchaInput = ""
                            }
                        }
                    )

                    Screen.Universities -> UniversityScreen(
                        universities = universities,
                        onUniversitySelected = { u -> selectedUniversity = u; screen = Screen.Groups },
                        onAddUniversity = { name ->
                            val clean = name.trim()
                            if (clean.isNotEmpty() && universities.none { it.equals(clean, ignoreCase = true) }) {
                                universities = universities + clean
                                saveUniversities(context, universities)
                            }
                        },
                        onEditUniversity = { oldName, newUnivName ->
                            val clean = newUnivName.trim()
                            if (clean.isNotEmpty() && universities.none { it != oldName && it.equals(clean, ignoreCase = true) }) {
                                universities = universities.map { if (it == oldName) clean else it }
                                groups = groups.map { if (it.university == oldName) it.copy(university = clean) else it }
                                students = students.map { if (it.college == oldName) it.copy(college = clean) else it }
                                if (selectedUniversity == oldName) selectedUniversity = clean
                                saveUniversities(context, universities)
                                saveGroups(context, groups)
                                saveAddedStudents(context, students)
                            }
                        },
                        onDeleteUniversity = { university ->
                            val deletedStudentList = students.filter { it.college == university }
                            deletedStudentIds = deletedStudentIds + deletedStudentList.map { it.id }.toSet()
                            saveDeletedStudentIds(context, deletedStudentIds)
                            students = students.filterNot { it.college == university }
                            groups = groups.filterNot { it.university == university }
                            universities = universities.filterNot { it == university }
                            saveUniversities(context, universities)
                            saveGroups(context, groups)
                            saveAddedStudents(context, students)
                            selectedStudent = null
                            selectedUniversity = universities.firstOrNull() ?: ""
                            selectedGroup = groups.firstOrNull { it.university == selectedUniversity }?.name ?: ""
                            screen = Screen.Universities
                        },
                        onBack = { screen = Screen.Captcha }
                    )

                    Screen.Groups -> {
                        val universityGroups = groups
                            .filter { it.university == selectedUniversity }
                            .map { it.name }
                        GroupScreen(
                            universityName = selectedUniversity,
                            groups = universityGroups,
                            onGroupSelected = { group -> selectedGroup = group; screen = Screen.StudentList },
                            onAddGroup = { name ->
                                val clean = name.trim()
                                val exists = groups.any {
                                    it.university == selectedUniversity && it.name.equals(clean, ignoreCase = true)
                                }
                                if (clean.isNotEmpty() && !exists) {
                                    groups = groups + GroupInfo(university = selectedUniversity, name = clean)
                                    saveGroups(context, groups)
                                }
                            },
                            onEditGroup = { oldName, newGroupName ->
                                val clean = newGroupName.trim()
                                val exists = groups.any {
                                    it.university == selectedUniversity && it.name != oldName && it.name.equals(clean, ignoreCase = true)
                                }
                                if (clean.isNotEmpty() && !exists) {
                                    groups = groups.map {
                                        if (it.university == selectedUniversity && it.name == oldName) it.copy(name = clean) else it
                                    }
                                    students = students.map {
                                        if (it.college == selectedUniversity && it.group == oldName) it.copy(group = clean) else it
                                    }
                                    if (selectedGroup == oldName) selectedGroup = clean
                                    saveGroups(context, groups)
                                    saveAddedStudents(context, students)
                                }
                            },
                            onDeleteGroup = { group ->
                                val deletedStudentList = students.filter {
                                    it.college == selectedUniversity && it.group == group
                                }
                                deletedStudentIds = deletedStudentIds + deletedStudentList.map { it.id }.toSet()
                                saveDeletedStudentIds(context, deletedStudentIds)
                                students = students.filterNot { it.college == selectedUniversity && it.group == group }
                                groups = groups.filterNot { it.university == selectedUniversity && it.name == group }
                                saveGroups(context, groups)
                                saveAddedStudents(context, students)
                                selectedStudent = null
                                selectedGroup = groups.firstOrNull { it.university == selectedUniversity }?.name ?: ""
                                screen = Screen.Groups
                            },
                            onBack = { screen = Screen.Universities }
                        )
                    }

                    Screen.StudentList -> {
                        val groupStudents = students.filter {
                            it.college == selectedUniversity && it.group == selectedGroup
                        }
                        StudentListScreen(
                            universityName = selectedUniversity,
                            groupName = selectedGroup,
                            students = groupStudents,
                            onStudentClick = { s -> selectedStudent = s; screen = Screen.StudentDetails },
                            onEditStudent = { s ->
                                editingStudent = s; newName = s.name; newId = s.id
                                newPhone = s.phone; newPhotoUri = s.imageUri; editError = ""
                                screen = Screen.EditStudent
                            },
                            onDeleteStudent = { s ->
                                students = students.filterNot { it.id == s.id }
                                deletedStudentIds = deletedStudentIds + s.id
                                saveDeletedStudentIds(context, deletedStudentIds)
                                saveAddedStudents(context, students)
                                selectedStudent = null
                            },
                            onAddStudent = {
                                newName = ""; newId = ""; newPhone = ""; newPhotoUri = null; addError = ""
                                screen = Screen.AddStudent
                            },
                            onBack = { screen = Screen.Groups }
                        )
                    }

                    Screen.AddStudent -> AddStudentScreen(
                        universityName = selectedUniversity,
                        groupName = selectedGroup,
                        name = newName,
                        id = newId,
                        phone = newPhone,
                        photoUri = newPhotoUri,
                        error = addError,
                        onNameChange = { newName = it },
                        onIdChange = { if (it.length <= 6 && it.all(Char::isDigit)) newId = it },
                        onPhoneChange = { if (it.length <= 15 && it.all(Char::isDigit)) newPhone = it },
                        onChoosePhoto = { imagePicker.launch(arrayOf("image/*")) },
                        onAdd = {
                            when {
                                newName.trim().isEmpty() -> addError = "Please enter student name."
                                newId.length != 6 -> addError = "Student ID must contain 6 digits."
                                newPhone.isEmpty() -> addError = "Please enter phone number."
                                newPhotoUri == null -> addError = "Please choose a student photo."
                                students.any { it.id == newId } -> addError = "This Student ID already exists."
                                selectedUniversity.isBlank() -> addError = "Please select a university first."
                                selectedGroup.isBlank() -> addError = "Please select a group first."
                                else -> {
                                    deletedStudentIds = deletedStudentIds - newId
                                    saveDeletedStudentIds(context, deletedStudentIds)
                                    val newStudent = Student(
                                        id = newId,
                                        name = newName.trim(),
                                        college = selectedUniversity,
                                        phone = newPhone,
                                        group = selectedGroup,
                                        color = studentCardPalette[students.size % studentCardPalette.size],
                                        imageResource = null,
                                        imageUri = newPhotoUri
                                    )
                                    students = students + newStudent
                                    saveAddedStudents(context, students)
                                    selectedStudent = newStudent
                                    screen = Screen.StudentDetails
                                }
                            }
                        },
                        onCancel = { screen = Screen.StudentList }
                    )

                    Screen.EditStudent -> editingStudent?.let { student ->
                        EditStudentScreen(
                            student = student,
                            universities = universities,
                            groups = groups,
                            name = newName,
                            id = newId,
                            phone = newPhone,
                            photoUri = newPhotoUri,
                            error = editError,
                            onNameChange = { newName = it },
                            onIdChange = { if (it.length <= 6 && it.all(Char::isDigit)) newId = it },
                            onPhoneChange = { if (it.length <= 15 && it.all(Char::isDigit)) newPhone = it },
                            onChoosePhoto = { imagePicker.launch(arrayOf("image/*")) },
                            onSave = { university, group ->
                                val cleanId = newId.trim()
                                val duplicate = students.any { it.id == cleanId && it.id != student.id }
                                when {
                                    newName.trim().isEmpty() -> editError = "Please enter student name."
                                    cleanId.length != 6 -> editError = "Student ID must contain 6 digits."
                                    newPhone.isEmpty() -> editError = "Please enter phone number."
                                    university.isBlank() -> editError = "Please select a university."
                                    group.isBlank() -> editError = "Please select a group."
                                    duplicate -> editError = "This Student ID already exists."
                                    else -> {
                                        val updated = student.copy(
                                            id = cleanId,
                                            name = newName.trim(),
                                            college = university,
                                            group = group,
                                            phone = newPhone,
                                            imageUri = newPhotoUri ?: student.imageUri
                                        )
                                        if (student.id != cleanId) {
                                            deletedStudentIds = deletedStudentIds + student.id - cleanId
                                            saveDeletedStudentIds(context, deletedStudentIds)
                                        }
                                        students = students.map { if (it.id == student.id) updated else it }
                                        saveAddedStudents(context, students)
                                        editingStudent = null
                                        selectedStudent = updated
                                        selectedUniversity = university
                                        selectedGroup = group
                                        screen = Screen.StudentDetails
                                    }
                                }
                            },
                            onCancel = { editingStudent = null; screen = Screen.StudentList }
                        )
                    }

                    Screen.StudentDetails -> selectedStudent?.let { student ->
                        StudentDetailsScreen(
                            student = student,
                            onEdit = {
                                editingStudent = student; newName = student.name
                                newId = student.id; newPhone = student.phone
                                newPhotoUri = student.imageUri; editError = ""
                                screen = Screen.EditStudent
                            },
                            onBack = { selectedStudent = null; screen = Screen.StudentList }
                        )
                    }
                }
            }

            if (showThemeDialog) {
                ThemeDialog(
                    selected = appThemeMode,
                    onSelect = { mode -> appThemeMode = mode; saveAppTheme(context, mode); showThemeDialog = false },
                    onDismiss = { showThemeDialog = false }
                )
            }
        }
    }
}

// ─── Default / Demo Student Data ──────────────────────────────────────────────

private fun buildDefaultStudents(): List<Student> = listOf(
    Student("654789", "Luffy", "KL University", "9558688699", "Anime Group", Color(0xFFFFCC80), R.drawable.student1),
    Student("654790", "Zoro gakusee", "KL University", "98765432111", "Anime Group", Color(0xFFBBDEFB), R.drawable.student2),
    Student("654791", "Sanji mikavo", "KL University", "98765432113", "Anime Group", Color(0xFFC8E6C9), R.drawable.student3),
    Student("654792", "Usoop techaru", "KL University", "98765432114", "Anime Group", Color(0xFFE1BEE7), R.drawable.student4),
    Student("654793", "Nico robin", "KL University", "98765432115", "Anime Group", Color(0xFFF8BBD0), R.drawable.student5),
    Student("654794", "Dora Doremon", "KL University", "9000000006", "Cartoon Group", Color(0xFFFFF9C4), R.drawable.student6),
    Student("654795", "Shinchan Nohara", "KL University", "9000000007", "Cartoon Group", Color(0xFFB2DFDB), R.drawable.student7),
    Student("654796", "Ninja Hatorii", "KL University", "9000000008", "Cartoon Group", Color(0xFFD1C4E9), R.drawable.student8),
    Student("654797", "Nobi Nobita", "KL University", "9000000009", "Cartoon Group", Color(0xFFFFCDD2), R.drawable.student9),
    Student("654798", "Red Ranger", "KL University", "9000000010", "Rangers Group", Color(0xFFFFCDD2), R.drawable.student10),
    Student("654799", "Blue Ranger", "KL University", "9000000011", "Rangers Group", Color(0xFFBBDEFB), R.drawable.student11),
    Student("654800", "Yellow Ranger", "KL University", "9000000012", "Rangers Group", Color(0xFFFFF59D), R.drawable.student12),
    Student("654801", "Green Ranger", "KL University", "9000000013", "Rangers Group", Color(0xFFC8E6C9), R.drawable.student13),
    Student("654802", "Black Ranger", "KL University", "9000000014", "Rangers Group", Color(0xFFD7CCC8), R.drawable.student14),
    Student("654803", "Pink Ranger", "KL University", "9000000015", "Rangers Group", Color(0xFFF8BBD0), R.drawable.student15),
)
