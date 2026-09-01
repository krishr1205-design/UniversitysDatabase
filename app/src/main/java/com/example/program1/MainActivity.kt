package com.example.program1

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.program1.ui.theme.Program1Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

data class Student(
    val id: String,
    val name: String,
    val college: String,
    val phone: String,
    val group: String,
    val color: Color,
    val imageResource: Int? = null,
    val imageUri: String? = null
)

data class GroupInfo(
    val university: String,
    val name: String
)

enum class AppThemeMode(val title: String) {
    OCEAN("Ocean Blue"),
    DARK("Dark"),
    FOREST("Forest Green"),
    PURPLE("Royal Purple"),
    SUNSET("Sunset Orange")
}

data class AppThemeColors(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val text: Color,
    val muted: Color,
    val onPrimary: Color
)

private val LocalAppTheme = staticCompositionLocalOf {
    AppThemeColors(
        background = Color(0xFFE3F2FD),
        surface = Color.White,
        primary = Color(0xFF1565C0),
        text = Color(0xFF111111),
        muted = Color(0xFF6B7280),
        onPrimary = Color.White
    )
}

fun appThemeColors(mode: AppThemeMode): AppThemeColors = when (mode) {
    AppThemeMode.OCEAN -> AppThemeColors(
        background = Color(0xFFE3F2FD),
        surface = Color.White,
        primary = Color(0xFF1565C0),
        text = Color(0xFF111111),
        muted = Color(0xFF607D8B),
        onPrimary = Color.White
    )
    AppThemeMode.DARK -> AppThemeColors(
        background = Color(0xFF101418),
        surface = Color(0xFF1B2229),
        primary = Color(0xFF64B5F6),
        text = Color(0xFFF3F4F6),
        muted = Color(0xFFB0BEC5),
        onPrimary = Color(0xFF0D1B2A)
    )
    AppThemeMode.FOREST -> AppThemeColors(
        background = Color(0xFFEAF4EC),
        surface = Color.White,
        primary = Color(0xFF2E7D32),
        text = Color(0xFF17351B),
        muted = Color(0xFF607D64),
        onPrimary = Color.White
    )
    AppThemeMode.PURPLE -> AppThemeColors(
        background = Color(0xFFF2ECFA),
        surface = Color.White,
        primary = Color(0xFF6A1B9A),
        text = Color(0xFF24152E),
        muted = Color(0xFF75657E),
        onPrimary = Color.White
    )
    AppThemeMode.SUNSET -> AppThemeColors(
        background = Color(0xFFFFF3E8),
        surface = Color.White,
        primary = Color(0xFFE65100),
        text = Color(0xFF321A0A),
        muted = Color(0xFF806957),
        onPrimary = Color.White
    )
}

fun appMaterialColors(mode: AppThemeMode): ColorScheme {
    val c = appThemeColors(mode)
    return if (mode == AppThemeMode.DARK) {
        darkColorScheme(
            primary = c.primary,
            onPrimary = c.onPrimary,
            secondary = Color(0xFF90CAF9),
            onSecondary = Color(0xFF102027),
            background = c.background,
            onBackground = c.text,
            surface = c.surface,
            onSurface = c.text,
            surfaceVariant = Color(0xFF263238),
            onSurfaceVariant = c.muted,
            error = Color(0xFFEF9A9A),
            onError = Color(0xFF3A0A0A)
        )
    } else {
        lightColorScheme(
            primary = c.primary,
            onPrimary = c.onPrimary,
            secondary = c.primary,
            onSecondary = c.onPrimary,
            background = c.background,
            onBackground = c.text,
            surface = c.surface,
            onSurface = c.text,
            surfaceVariant = c.background,
            onSurfaceVariant = c.muted,
            error = Color(0xFFC62828),
            onError = Color.White
        )
    }
}

@Composable
fun themePrimary(): Color = LocalAppTheme.current.primary

@Composable
fun themeBackground(): Color = LocalAppTheme.current.background

@Composable
fun themeSurface(): Color = LocalAppTheme.current.surface

@Composable
fun themeText(): Color = LocalAppTheme.current.text

@Composable
fun themeMuted(): Color = LocalAppTheme.current.muted

@Composable
fun themeOnPrimary(): Color = LocalAppTheme.current.onPrimary

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

    var screen by remember { mutableStateOf(1) }

    var captchaCode by remember { mutableStateOf(generateCaptcha()) }
    var captchaInput by remember { mutableStateOf("") }
    var captchaError by remember { mutableStateOf(false) }

    var selectedUniversity by remember { mutableStateOf("KL University") }
    var selectedGroup by remember { mutableStateOf("Anime Group") }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }

    var editingStudent by remember { mutableStateOf<Student?>(null) }

    var newName by remember { mutableStateOf("") }
    var newId by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newPhotoUri by remember { mutableStateOf<String?>(null) }
    var addError by remember { mutableStateOf("") }
    var editError by remember { mutableStateOf("") }
    var appThemeMode by remember { mutableStateOf(loadAppTheme(context)) }
    var showThemeDialog by remember { mutableStateOf(false) }

    val defaultUniversity = "KL University"

    val defaultGroupNames = remember {
        listOf("Anime Group", "Cartoon Group", "Rangers Group")
    }

    val storedUniversities = remember {
        loadSavedUniversities(context)
    }

    var universities by remember {
        mutableStateOf(
            if (hasSavedUniversities(context)) {
                storedUniversities
            } else {
                listOf(defaultUniversity)
            }
        )
    }

    LaunchedEffect(Unit) {
        if (storedUniversities.isNotEmpty()) {
            saveUniversities(context, storedUniversities)
        }
    }

    val storedGroups = remember {
        loadSavedGroups(context)
    }

    var groups by remember {
        mutableStateOf(
            if (hasSavedGroups(context)) {
                storedGroups
            } else {
                defaultGroupNames.map {
                    GroupInfo(
                        university = defaultUniversity,
                        name = it
                    )
                }
            }
        )
    }

    var deletedStudentIds by remember {
        mutableStateOf(loadDeletedStudentIds(context).toSet())
    }

    val defaultStudents = remember {

        listOf(

            Student(
                id = "654789",
                name = "Luffy",
                college = "KL University",
                phone = "9558688699",
                group = "Anime Group",
                color = Color(0xFFFFCC80),
                imageResource = R.drawable.student1
            ),

            Student(
                id = "654790",
                name = "Zoro gakusee",
                college = "KL University",
                phone = "98765432111",
                group = "Anime Group",
                color = Color(0xFFBBDEFB),
                imageResource = R.drawable.student2
            ),

            Student(
                id = "654791",
                name = "Sanji mikavo",
                college = "KL University",
                phone = "98765432113",
                group = "Anime Group",
                color = Color(0xFFC8E6C9),
                imageResource = R.drawable.student3
            ),

            Student(
                id = "654792",
                name = "Usoop techaru",
                college = "KL University",
                phone = "98765432114",
                group = "Anime Group",
                color = Color(0xFFE1BEE7),
                imageResource = R.drawable.student4
            ),

            Student(
                id = "654793",
                name = "Nico robin",
                college = "KL University",
                phone = "98765432115",
                group = "Anime Group",
                color = Color(0xFFF8BBD0),
                imageResource = R.drawable.student5
            ),

            Student(
                id = "654794",
                name = "Dora Doremon",
                college = "KL University",
                phone = "9000000006",
                group = "Cartoon Group",
                color = Color(0xFFFFF9C4),
                imageResource = R.drawable.student6
            ),

            Student(
                id = "654795",
                name = "Shinchan Nohara",
                college = "KL University",
                phone = "9000000007",
                group = "Cartoon Group",
                color = Color(0xFFB2DFDB),
                imageResource = R.drawable.student7
            ),

            Student(
                id = "654796",
                name = "Ninja Hatorii",
                college = "KL University",
                phone = "9000000008",
                group = "Cartoon Group",
                color = Color(0xFFD1C4E9),
                imageResource = R.drawable.student8
            ),

            Student(
                id = "654797",
                name = "Nobi Nobita",
                college = "KL University",
                phone = "9000000009",
                group = "Cartoon Group",
                color = Color(0xFFFFCDD2),
                imageResource = R.drawable.student9
            ),

            Student(
                id = "654798",
                name = "Red Ranger",
                college = "KL University",
                phone = "9000000010",
                group = "Rangers Group",
                color = Color(0xFFFFCDD2),
                imageResource = R.drawable.student10
            ),

            Student(
                id = "654799",
                name = "Blue Ranger",
                college = "KL University",
                phone = "9000000011",
                group = "Rangers Group",
                color = Color(0xFFBBDEFB),
                imageResource = R.drawable.student11
            ),

            Student(
                id = "654800",
                name = "Yellow Ranger",
                college = "KL University",
                phone = "9000000012",
                group = "Rangers Group",
                color = Color(0xFFFFF59D),
                imageResource = R.drawable.student12
            ),

            Student(
                id = "654801",
                name = "Green Ranger",
                college = "KL University",
                phone = "9000000013",
                group = "Rangers Group",
                color = Color(0xFFC8E6C9),
                imageResource = R.drawable.student13
            ),

            Student(
                id = "654802",
                name = "Black Ranger",
                college = "KL University",
                phone = "9000000014",
                group = "Rangers Group",
                color = Color(0xFFD7CCC8),
                imageResource = R.drawable.student14
            ),

            Student(
                id = "654803",
                name = "Pink Ranger",
                college = "KL University",
                phone = "9000000015",
                group = "Rangers Group",
                color = Color(0xFFF8BBD0),
                imageResource = R.drawable.student15
            )
        )
    }

    var students by remember {
        mutableStateOf(
            (loadSavedStudents(context) + defaultStudents)
                .distinctBy { it.id }
                .filter { it.id !in deletedStudentIds }
        )
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }
            newPhotoUri = uri.toString()
        }
    }

    val appColors = appThemeColors(appThemeMode)

    MaterialTheme(
        colorScheme = appMaterialColors(appThemeMode)
    ) {
        CompositionLocalProvider(
            LocalAppTheme provides appColors,
            LocalContentColor provides appColors.text
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = appColors.background
            ) {
                when (screen) {

                    1 -> WelcomeScreen(
                        onStart = {
                            captchaCode = generateCaptcha()
                            captchaInput = ""
                            captchaError = false
                            screen = 2
                        },
                        onTheme = { showThemeDialog = true },
                        themeName = appThemeMode.title
                    )

                    2 -> CaptchaScreen(
                        captchaCode = captchaCode,
                        captchaInput = captchaInput,
                        captchaError = captchaError,
                        onInputChange = {
                            captchaInput = it
                            captchaError = false
                        },
                        onRefresh = {
                            captchaCode = generateCaptcha()
                            captchaInput = ""
                            captchaError = false
                        },
                        onVerify = {
                            if (captchaInput.trim().equals(captchaCode, ignoreCase = true)) {
                                captchaInput = ""
                                captchaError = false
                                screen = 3
                            } else {
                                captchaError = true
                                captchaCode = generateCaptcha()
                                captchaInput = ""
                            }
                        }
                    )

                    3 -> UniversityScreen(
                        universities = universities,
                        onUniversitySelected = { university ->
                            selectedUniversity = university
                            screen = 4
                        },
                        onAddUniversity = { name ->
                            val clean = name.trim()
                            if (
                                clean.isNotEmpty() &&
                                universities.none { it.equals(clean, ignoreCase = true) }
                            ) {
                                universities = universities + clean
                                saveUniversities(context, universities)
                            }
                        },
                        onEditUniversity = { oldName, newName ->
                            val clean = newName.trim()
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
                            val deletedGroupNames = groups
                                .filter { it.university == university }
                                .map { it.name }
                                .toSet()

                            val deletedStudents = students.filter {
                                it.college == university
                            }

                            deletedStudentIds = deletedStudentIds +
                                    deletedStudents.map { it.id }.toSet()

                            saveDeletedStudentIds(context, deletedStudentIds)

                            students = students.filterNot {
                                it.college == university
                            }

                            groups = groups.filterNot {
                                it.university == university
                            }

                            universities = universities.filterNot {
                                it == university
                            }

                            saveUniversities(context, universities)
                            saveGroups(context, groups)
                            saveAddedStudents(
                                context,
                                students
                            )

                            selectedStudent = null

                            selectedUniversity = universities.firstOrNull() ?: ""
                            selectedGroup = groups
                                .firstOrNull { it.university == selectedUniversity }
                                ?.name
                                ?: ""

                            screen = 3
                        },
                        onBack = {
                            screen = 2
                        }
                    )

                    4 -> {
                        val universityGroups = groups
                            .filter { it.university == selectedUniversity }
                            .map { it.name }

                        GroupScreen(
                            universityName = selectedUniversity,
                            groups = universityGroups,
                            onGroupSelected = { group ->
                                selectedGroup = group
                                screen = 5
                            },
                            onAddGroup = { name ->
                                val clean = name.trim()
                                val alreadyExists = groups.any {
                                    it.university == selectedUniversity &&
                                            it.name.equals(clean, ignoreCase = true)
                                }

                                if (clean.isNotEmpty() && !alreadyExists) {
                                    groups = groups + GroupInfo(
                                        university = selectedUniversity,
                                        name = clean
                                    )
                                    saveGroups(context, groups)
                                }
                            },
                            onEditGroup = { oldName, newName ->
                                val clean = newName.trim()
                                val exists = groups.any {
                                    it.university == selectedUniversity &&
                                            it.name != oldName &&
                                            it.name.equals(clean, ignoreCase = true)
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
                                val deletedStudents = students.filter {
                                    it.college == selectedUniversity &&
                                            it.group == group
                                }

                                deletedStudentIds = deletedStudentIds +
                                        deletedStudents.map { it.id }.toSet()

                                saveDeletedStudentIds(context, deletedStudentIds)

                                students = students.filterNot {
                                    it.college == selectedUniversity &&
                                            it.group == group
                                }

                                groups = groups.filterNot {
                                    it.university == selectedUniversity &&
                                            it.name == group
                                }

                                saveGroups(context, groups)
                                saveAddedStudents(
                                    context,
                                    students
                                )

                                selectedStudent = null
                                selectedGroup = groups
                                    .firstOrNull { it.university == selectedUniversity }
                                    ?.name
                                    ?: ""

                                screen = 4
                            },
                            onBack = {
                                screen = 3
                            }
                        )
                    }

                    5 -> {
                        val groupStudents = students.filter {
                            it.college == selectedUniversity &&
                                    it.group == selectedGroup
                        }

                        StudentListScreen(
                            universityName = selectedUniversity,
                            groupName = selectedGroup,
                            students = groupStudents,
                            onStudentClick = { student ->
                                selectedStudent = student
                                screen = 7
                            },
                            onEditStudent = { student ->
                                editingStudent = student
                                newName = student.name
                                newId = student.id
                                newPhone = student.phone
                                newPhotoUri = student.imageUri
                                editError = ""
                                screen = 8
                            },
                            onDeleteStudent = { student ->
                                students = students.filterNot { it.id == student.id }
                                deletedStudentIds = deletedStudentIds + student.id
                                saveDeletedStudentIds(context, deletedStudentIds)
                                saveAddedStudents(
                                    context,
                                    students
                                )
                                selectedStudent = null
                            },
                            onAddStudent = {
                                newName = ""
                                newId = ""
                                newPhone = ""
                                newPhotoUri = null
                                addError = ""
                                screen = 6
                            },
                            onBack = {
                                screen = 4
                            }
                        )
                    }

                    6 -> AddStudentScreen(
                        universityName = selectedUniversity,
                        groupName = selectedGroup,
                        name = newName,
                        id = newId,
                        phone = newPhone,
                        photoUri = newPhotoUri,
                        error = addError,
                        onNameChange = { newName = it },
                        onIdChange = {
                            if (it.length <= 6 && it.all(Char::isDigit)) {
                                newId = it
                            }
                        },
                        onPhoneChange = {
                            if (it.length <= 15 && it.all(Char::isDigit)) {
                                newPhone = it
                            }
                        },
                        onChoosePhoto = {
                            imagePicker.launch(arrayOf("image/*"))
                        },
                        onAdd = {
                            when {
                                newName.trim().isEmpty() -> {
                                    addError = "Please enter student name."
                                }
                                newId.length != 6 -> {
                                    addError = "Student ID must contain 6 digits."
                                }
                                newPhone.isEmpty() -> {
                                    addError = "Please enter phone number."
                                }
                                newPhotoUri == null -> {
                                    addError = "Please choose a student photo."
                                }
                                students.any { it.id == newId } -> {
                                    addError = "This Student ID already exists."
                                }
                                selectedUniversity.isBlank() -> {
                                    addError = "Please select a university first."
                                }
                                selectedGroup.isBlank() -> {
                                    addError = "Please select a group first."
                                }
                                else -> {
                                    deletedStudentIds = deletedStudentIds - newId
                                    saveDeletedStudentIds(context, deletedStudentIds)

                                    val colors = listOf(
                                        Color(0xFFFFE0B2),
                                        Color(0xFFB2EBF2),
                                        Color(0xFFC5E1A5),
                                        Color(0xFFD1C4E9),
                                        Color(0xFFFFCDD2),
                                        Color(0xFFFFF9C4),
                                        Color(0xFFB3E5FC),
                                        Color(0xFFFFCCBC)
                                    )

                                    val newStudent = Student(
                                        id = newId,
                                        name = newName.trim(),
                                        college = selectedUniversity,
                                        phone = newPhone,
                                        group = selectedGroup,
                                        color = colors[students.size % colors.size],
                                        imageResource = null,
                                        imageUri = newPhotoUri
                                    )

                                    students = students + newStudent

                                    saveAddedStudents(
                                        context,
                                        students
                                    )

                                    selectedStudent = newStudent
                                    screen = 7
                                }
                            }
                        },
                        onCancel = {
                            screen = 5
                        }
                    )

                    8 -> editingStudent?.let { student ->
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
                            onIdChange = {
                                if (it.length <= 6 && it.all(Char::isDigit)) newId = it
                            },
                            onPhoneChange = {
                                if (it.length <= 15 && it.all(Char::isDigit)) newPhone = it
                            },
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
                                        screen = 7
                                    }
                                }
                            },
                            onCancel = { editingStudent = null; screen = 5 }
                        )
                    }

                    7 -> selectedStudent?.let { student ->
                        StudentDetailsScreen(
                            student = student,
                            onEdit = {
                                editingStudent = student
                                newName = student.name
                                newId = student.id
                                newPhone = student.phone
                                newPhotoUri = student.imageUri
                                editError = ""
                                screen = 8
                            },
                            onBack = {
                                selectedStudent = null
                                screen = 5
                            }
                        )
                    }
                }
            }

            if (showThemeDialog) {
                ThemeDialog(
                    selected = appThemeMode,
                    onSelect = { mode ->
                        appThemeMode = mode
                        saveAppTheme(context, mode)
                        showThemeDialog = false
                    },
                    onDismiss = { showThemeDialog = false }
                )
            }
        }
    }
}

@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
    onTheme: () -> Unit,
    themeName: String
) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(20.dp),

        verticalArrangement =
            Arrangement.Center,

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = "UNIVERSITY'S DATABASES",

            fontSize = 32.sp,

            fontWeight = FontWeight.Bold,

            color = themePrimary()
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Text(
            text = "CLASSMATE DIRECTORY",

            fontSize = 26.sp,

            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Text(
            text = "WELCOME",

            fontSize = 22.sp,

            color =
                themeMuted()
        )

        Spacer(
            modifier =
                Modifier.height(30.dp)
        )

        Button(
            onClick = onStart,
            colors = ButtonDefaults.buttonColors(
                containerColor = themePrimary(),
                contentColor = themeOnPrimary()
            )
        ) {
            Text("START")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(onClick = onTheme) {
            Text("THEME: $themeName")
        }
    }
}

@Composable
fun ThemeDialog(
    selected: AppThemeMode,
    onSelect: (AppThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Theme") },
        text = {
            Column {
                AppThemeMode.values().forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(mode) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = mode == selected,
                            onClick = { onSelect(mode) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(mode.title)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("CLOSE") }
        }
    )
}

@Composable
fun CaptchaScreen(

    captchaCode: String,

    captchaInput: String,

    captchaError: Boolean,

    onInputChange: (String) -> Unit,

    onRefresh: () -> Unit,

    onVerify: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(20.dp),

        verticalArrangement =
            Arrangement.Center,

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = "CAPTCHA",

            fontSize = 30.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                themePrimary()
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text = "Security Verification",

            fontSize = 20.sp,

            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Card(

            modifier =
                Modifier
                    .width(280.dp)
                    .height(110.dp),

            shape =
                RoundedCornerShape(8.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        themeSurface()
                )
        ) {

            CaptchaImage(
                captchaCode =
                    captchaCode
            )
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        OutlinedButton(
            onClick = onRefresh
        ) {

            Text("? New CAPTCHA")
        }

        Spacer(
            modifier =
                Modifier.height(18.dp)
        )

        OutlinedTextField(

            value =
                captchaInput,

            onValueChange =
                onInputChange,

            label = {
                Text("Enter CAPTCHA")
            },

            singleLine = true
        )

        if (captchaError) {

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "Incorrect CAPTCHA. Try again.",

                color =
                    Color.Red,

                fontWeight =
                    FontWeight.Bold
            )
        }

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Button(
            onClick = onVerify
        ) {

            Text("VERIFY")
        }
    }
}

@Composable
fun UniversityScreen(
    universities: List<String>,
    onUniversitySelected: (String) -> Unit,
    onAddUniversity: (String) -> Unit,
    onEditUniversity: (String, String) -> Unit,
    onDeleteUniversity: (String) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var universityName by remember { mutableStateOf("") }

    val colors = listOf(
        Color(0xFFFFCC80),
        Color(0xFFB3E5FC),
        Color(0xFFFFCDD2),
        Color(0xFFC8E6C9),
        Color(0xFFD1C4E9),
        Color(0xFFFFF9C4),
        Color(0xFFFFCCBC),
        Color(0xFFB2DFDB)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        Text(
            "SELECT UNIVERSITY",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = themePrimary()
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Choose a university to manage its groups and students.",
            color = themeMuted(),
            fontSize = 14.sp
        )

        Spacer(Modifier.height(24.dp))

        if (universities.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = themeSurface()),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No universities added yet.",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Add a university to start creating groups and students.",
                        color = themeMuted()
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        } else {
            universities.forEachIndexed { index, university ->
                UniversityRow(
                    text = university,
                    color = colors[index % colors.size],
                    onClick = { onUniversitySelected(university) },
                    onEdit = { newName -> onEditUniversity(university, newName) },
                    onDelete = { onDeleteUniversity(university) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }

        Button(
            onClick = {
                universityName = ""
                showAddDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = themePrimary()
            )
        ) {
            Text("+ ADD UNIVERSITY")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("BACK TO CAPTCHA")
        }

        Spacer(Modifier.height(20.dp))
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("ADD UNIVERSITY") },
            text = {
                OutlinedTextField(
                    value = universityName,
                    onValueChange = { universityName = it },
                    label = { Text("University Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = universityName.trim()
                        if (clean.isNotEmpty()) {
                            onAddUniversity(clean)
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("ADD")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
fun UniversityRow(
    text: String,
    color: Color,
    onClick: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember(text) { mutableStateOf(text) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .height(70.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = color)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.width(6.dp))

        OutlinedButton(
            onClick = { editName = text; showEditDialog = true },
            modifier = Modifier.height(70.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) { Text("EDIT", color = themePrimary()) }

        Spacer(Modifier.width(6.dp))

        OutlinedButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.height(70.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Text("DELETE", color = Color(0xFFC62828))
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("EDIT UNIVERSITY") },
            text = {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("University Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    val clean = editName.trim()
                    if (clean.isNotEmpty()) { onEdit(clean); showEditDialog = false }
                }) { Text("SAVE") }
            },
            dismissButton = { OutlinedButton(onClick = { showEditDialog = false }) { Text("CANCEL") } }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("DELETE UNIVERSITY?") },
            text = {
                Text(
                    "Delete \"$text\" and everything inside it?\n\n" +
                            "This will remove all groups and students belonging to this university. " +
                            "This action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC62828)
                    )
                ) {
                    Text("DELETE")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
fun GroupScreen(
    universityName: String,
    groups: List<String>,
    onGroupSelected: (String) -> Unit,
    onAddGroup: (String) -> Unit,
    onEditGroup: (String, String) -> Unit,
    onDeleteGroup: (String) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }

    val groupColors = listOf(
        Color(0xFFFFCC80),
        Color(0xFFB3E5FC),
        Color(0xFFFFCDD2),
        Color(0xFFC8E6C9),
        Color(0xFFD1C4E9),
        Color(0xFFFFF9C4),
        Color(0xFFFFCCBC),
        Color(0xFFB2DFDB)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        Text(
            "SELECT GROUP",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = themePrimary()
        )

        Spacer(Modifier.height(6.dp))

        Text(
            universityName.ifBlank { "No University Selected" },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        Spacer(Modifier.height(22.dp))

        if (groups.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = themeSurface()),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    "No groups in this university yet.",
                    modifier = Modifier.padding(20.dp),
                    color = themeMuted()
                )
            }
            Spacer(Modifier.height(12.dp))
        } else {
            groups.forEachIndexed { index, group ->
                GroupRow(
                    text = group,
                    color = groupColors[index % groupColors.size],
                    canDelete = true,
                    onClick = { onGroupSelected(group) },
                    onEdit = { newName -> onEditGroup(group, newName) },
                    onDelete = { onDeleteGroup(group) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }

        Button(
            onClick = {
                groupName = ""
                showAddDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = themePrimary()
            )
        ) {
            Text("+ ADD GROUP")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("BACK TO UNIVERSITIES")
        }

        Spacer(Modifier.height(20.dp))
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("ADD GROUP") },
            text = {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Group Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = groupName.trim()
                        if (clean.isNotEmpty()) {
                            onAddGroup(clean)
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("ADD")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
fun GroupRow(
    text: String,
    color: Color,
    canDelete: Boolean,
    onClick: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember(text) { mutableStateOf(text) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .height(70.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = color)
        ) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.width(6.dp))
        OutlinedButton(
            onClick = { editName = text; showEditDialog = true },
            modifier = Modifier.height(70.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) { Text("EDIT", color = themePrimary()) }

        if (canDelete) {
            Spacer(Modifier.width(6.dp))
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.height(70.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("DELETE", color = Color(0xFFC62828))
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("EDIT GROUP") },
            text = {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Group Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    val clean = editName.trim()
                    if (clean.isNotEmpty()) { onEdit(clean); showEditDialog = false }
                }) { Text("SAVE") }
            },
            dismissButton = { OutlinedButton(onClick = { showEditDialog = false }) { Text("CANCEL") } }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("DELETE GROUP?") },
            text = {
                Text(
                    "Delete \"$text\" and all students in this group?\n\n" +
                            "This action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC62828)
                    )
                ) {
                    Text("DELETE")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
fun GroupButton(

    text: String,

    color: Color,

    onClick: () -> Unit
) {

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clickable {
                    onClick()
                },

        shape =
            RoundedCornerShape(18.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    color
            )
    ) {

        Box(

            modifier =
                Modifier.fillMaxSize(),

            contentAlignment =
                Alignment.Center
        ) {

            Text(

                text = text,

                fontSize = 21.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}

@Composable
fun StudentListScreen(
    universityName: String,
    groupName: String,
    students: List<Student>,
    onStudentClick: (Student) -> Unit,
    onEditStudent: (Student) -> Unit,
    onDeleteStudent: (Student) -> Unit,
    onAddStudent: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Text(groupName, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = themePrimary())
        Text(universityName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Text("${students.size} student${if (students.size == 1) "" else "s"}", color = themeMuted())
        Spacer(Modifier.height(18.dp))

        if (students.isEmpty()) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = themeSurface())) {
                Text("No students in this group yet.", modifier = Modifier.padding(20.dp), color = themeMuted())
            }
            Spacer(Modifier.height(12.dp))
        } else {
            students.forEach { student ->
                StudentCard(student = student, onClick = { onStudentClick(student) }, onEdit = { onEditStudent(student) }, onDelete = { onDeleteStudent(student) })
                Spacer(Modifier.height(10.dp))
            }
        }

        Button(
            onClick = onAddStudent,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            modifier = Modifier.fillMaxWidth()
        ) { Text("+ ADD STUDENT") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("BACK TO GROUPS") }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun StudentCard(
    student: Student,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Card(
            modifier = Modifier.weight(1f).height(82.dp).clickable(onClick = onClick),
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = student.color)
        ) {
            Row(Modifier.fillMaxSize().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                StudentImage(student, 55.dp)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(student.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("ID: ${student.id}", fontSize = 14.sp, color = Color.DarkGray)
                }
            }
        }
        Spacer(Modifier.width(6.dp))
        OutlinedButton(
            onClick = onEdit,
            modifier = Modifier.height(82.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) { Text("EDIT", color = themePrimary()) }
        Spacer(Modifier.width(6.dp))
        OutlinedButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.height(82.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) { Text("DELETE", color = Color(0xFFC62828)) }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("DELETE STUDENT?") },
            text = { Text("Delete ${student.name} permanently from the app?") },
            confirmButton = {
                Button(
                    onClick = { showDeleteDialog = false; onDelete() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) { Text("DELETE") }
            },
            dismissButton = { OutlinedButton(onClick = { showDeleteDialog = false }) { Text("CANCEL") } }
        )
    }
}

@Composable
fun EditStudentScreen(
    student: Student,
    universities: List<String>,
    groups: List<GroupInfo>,
    name: String,
    id: String,
    phone: String,
    photoUri: String?,
    error: String,
    onNameChange: (String) -> Unit,
    onIdChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onChoosePhoto: () -> Unit,
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    var university by remember(student.id) { mutableStateOf(student.college) }
    var group by remember(student.id) { mutableStateOf(student.group) }
    var showUniversityMenu by remember { mutableStateOf(false) }
    var showGroupMenu by remember { mutableStateOf(false) }
    val availableGroups = groups
        .filter { it.university == university }
        .map { it.name }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Text("EDIT STUDENT", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = themePrimary())
        Spacer(Modifier.height(20.dp))

        if (photoUri != null) LocalPhoto(photoUri, Modifier.size(150.dp))
        else StudentImage(student, 150.dp)

        Spacer(Modifier.height(12.dp))
        Button(onClick = onChoosePhoto) { Text("CHANGE PHOTO") }
        Spacer(Modifier.height(18.dp))

        OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Student Name") }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = id, onValueChange = onIdChange, label = { Text("Student ID") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = phone, onValueChange = onPhoneChange, label = { Text("Phone Number") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(Modifier.height(12.dp))

        Box(Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { showUniversityMenu = true }, modifier = Modifier.fillMaxWidth()) {
                Text("University: $university")
            }
            DropdownMenu(expanded = showUniversityMenu, onDismissRequest = { showUniversityMenu = false }) {
                universities.forEach { item ->
                    DropdownMenuItem(text = { Text(item) }, onClick = {
                        university = item
                        group = ""
                        showUniversityMenu = false
                    })
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Box(Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { showGroupMenu = true }, modifier = Modifier.fillMaxWidth(), enabled = availableGroups.isNotEmpty()) {
                Text(if (group.isBlank()) "Select Group" else "Group: $group")
            }
            DropdownMenu(expanded = showGroupMenu, onDismissRequest = { showGroupMenu = false }) {
                availableGroups.forEach { item ->
                    DropdownMenuItem(text = { Text(item) }, onClick = { group = item; showGroupMenu = false })
                }
            }
        }

        if (error.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text(error, color = Color.Red, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(20.dp))
        Button(onClick = { onSave(university, group) }, colors = ButtonDefaults.buttonColors(containerColor = themePrimary())) { Text("SAVE CHANGES") }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onCancel) { Text("CANCEL") }
        Spacer(Modifier.height(25.dp))
    }
}

@Composable
fun AddStudentScreen(

    universityName: String,

    groupName: String,

    name: String,

    id: String,

    phone: String,

    photoUri: String?,

    error: String,

    onNameChange: (String) -> Unit,

    onIdChange: (String) -> Unit,

    onPhoneChange: (String) -> Unit,

    onChoosePhoto: () -> Unit,

    onAdd: () -> Unit,

    onCancel: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(
                    rememberScrollState()
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Text(
            text = "ADD STUDENT",

            fontSize = 30.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                themePrimary()
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                "University: $universityName",

            color =
                themeMuted()
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        Text(
            text =
                "Group: $groupName",

            color =
                themeMuted()
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        if (photoUri != null) {

            LocalPhoto(
                uriString =
                    photoUri,

                modifier =
                    Modifier.size(150.dp)
            )

        } else {

            Box(

                modifier =
                    Modifier
                        .size(150.dp)
                        .background(
                            Color.LightGray,
                            RoundedCornerShape(15.dp)
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "PHOTO",

                    fontSize = 20.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(15.dp)
        )

        Button(
            onClick =
                onChoosePhoto
        ) {

            Text("CHOOSE PHOTO")
        }

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        OutlinedTextField(

            value =
                name,

            onValueChange =
                onNameChange,

            label = {
                Text("Student Name")
            },

            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        OutlinedTextField(

            value =
                id,

            onValueChange =
                onIdChange,

            label = {
                Text("Student ID")
            },

            singleLine = true,

            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Number
                )
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        OutlinedTextField(

            value =
                phone,

            onValueChange =
                onPhoneChange,

            label = {
                Text("Phone Number")
            },

            singleLine = true,

            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Phone
                )
        )

        if (error.isNotEmpty()) {

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(
                text = error,

                color =
                    Color.Red,

                fontWeight =
                    FontWeight.Bold
            )
        }

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Button(
            onClick =
                onAdd,

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        Color(0xFF2E7D32)
                )
        ) {

            Text("ADD STUDENT")
        }

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        OutlinedButton(
            onClick =
                onCancel
        ) {

            Text("CANCEL")
        }

        Spacer(
            modifier =
                Modifier.height(25.dp)
        )
    }
}

@Composable
fun StudentDetailsScreen(

    student: Student,

    onEdit: () -> Unit,

    onBack: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    student.color
                )
                .padding(20.dp),

        verticalArrangement =
            Arrangement.Center,

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Card(

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(20.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        themeSurface()
                )
        ) {

            Column(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                StudentImage(
                    student =
                        student,

                    size =
                        160.dp
                )

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )

                Text(

                    text =
                        "STUDENT INFORMATION",

                    fontSize =
                        25.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color(0xFFE65100)
                )

                Spacer(
                    modifier =
                        Modifier.height(25.dp)
                )

                DetailItem(
                    title =
                        "Full Name",

                    value =
                        student.name
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                DetailItem(
                    title =
                        "Student ID",

                    value =
                        student.id
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                DetailItem(
                    title =
                        "Group",

                    value =
                        student.group
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                DetailItem(
                    title =
                        "University",

                    value =
                        student.college
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                DetailItem(
                    title =
                        "Contact Number",

                    value =
                        student.phone
                )

                Spacer(
                    modifier =
                        Modifier.height(25.dp)
                )

                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themePrimary()
                    )
                ) {
                    Text("EDIT DATA", color = themeOnPrimary())
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onBack
                ) {
                    Text("BACK")
                }
            }
        }
    }
}

@Composable
fun DetailItem(

    title: String,

    value: String
) {

    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(

            text = title,

            fontSize = 14.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                themeMuted()
        )

        Text(

            text = value,

            fontSize = 18.sp,

            fontWeight =
                FontWeight.Medium
        )
    }
}

@Composable
fun StudentImage(

    student: Student,

    size: androidx.compose.ui.unit.Dp
) {

    if (student.imageUri != null) {

        LocalPhoto(

            uriString =
                student.imageUri,

            modifier =
                Modifier.size(size)
        )

    } else if (student.imageResource != null) {

        Image(

            painter =
                painterResource(
                    id =
                        student.imageResource
                ),

            contentDescription =
                "Student photo",

            modifier =
                Modifier.size(size),

            contentScale =
                ContentScale.Crop
        )
    }
}

@Composable
fun LocalPhoto(

    uriString: String,

    modifier: Modifier
) {

    val context =
        LocalContext.current

    var bitmap by remember(uriString) {
        mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null)
    }

    LaunchedEffect(uriString) {

        bitmap =
            withContext(Dispatchers.IO) {

                try {

                    val uri =
                        Uri.parse(uriString)

                    context.contentResolver
                        .openInputStream(uri)
                        ?.use { inputStream ->

                            BitmapFactory
                                .decodeStream(inputStream)
                                ?.asImageBitmap()
                        }

                } catch (_: Exception) {

                    null
                }
            }
    }

    if (bitmap != null) {

        Image(

            bitmap =
                bitmap!!,

            contentDescription =
                "Student photo",

            modifier =
                modifier,

            contentScale =
                ContentScale.Crop
        )

    } else {

        Box(

            modifier =
                modifier.background(
                    Color.LightGray,
                    RoundedCornerShape(10.dp)
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Text("Photo")
        }
    }
}

@Composable
fun CaptchaImage(

    captchaCode: String
) {

    Canvas(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(5.dp)
    ) {

        drawRect(
            color =
                Color(0xFFF7F7F7)
        )

        repeat(180) {

            val x =
                Random.nextFloat() *
                        size.width

            val y =
                Random.nextFloat() *
                        size.height

            val radius =
                Random.nextFloat() * 2.5f +
                        0.5f

            drawCircle(

                color =
                    Color(
                        red =
                            Random.nextInt(
                                80,
                                200
                            ),

                        green =
                            Random.nextInt(
                                80,
                                200
                            ),

                        blue =
                            Random.nextInt(
                                80,
                                200
                            )
                    ),

                radius =
                    radius,

                center =
                    Offset(x, y)
            )
        }

        repeat(6) {

            drawLine(

                color =
                    Color(0xFF78909C),

                start =
                    Offset(
                        Random.nextFloat() *
                                size.width,

                        Random.nextFloat() *
                                size.height
                    ),

                end =
                    Offset(
                        Random.nextFloat() *
                                size.width,

                        Random.nextFloat() *
                                size.height
                    ),

                strokeWidth =
                    1.5f
            )
        }

        val characterWidth =
            size.width /
                    (captchaCode.length + 1)

        captchaCode.forEachIndexed {
                index,
                character ->

            val x =
                characterWidth *
                        (index + 1)

            val y =
                size.height / 2f +
                        17.dp.toPx()

            val rotation =
                Random.nextFloat() *
                        40f - 20f

            rotate(

                degrees =
                    rotation,

                pivot =
                    Offset(x, y)
            ) {

                drawContext
                    .canvas
                    .nativeCanvas
                    .drawText(

                        character.toString(),

                        x,

                        y,

                        android.graphics.Paint()
                            .apply {

                                isAntiAlias =
                                    true

                                textSize =
                                    42.dp.toPx()

                                typeface =
                                    android.graphics.Typeface
                                        .create(
                                            android.graphics.Typeface.SERIF,
                                            android.graphics.Typeface.BOLD
                                        )

                                color =
                                    android.graphics.Color.BLACK
                            }
                    )
            }
        }

        repeat(3) {

            val y =
                Random.nextFloat() *
                        size.height

            drawLine(

                color =
                    Color(0xFF9E9E9E),

                start =
                    Offset(0f, y),

                end =
                    Offset(size.width, y),

                strokeWidth =
                    1f
            )
        }
    }
}

fun generateCaptcha(): String {

    val characters =
        "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

    return (1..5)
        .map {

            characters[
                Random.nextInt(
                    characters.length
                )
            ]
        }
        .joinToString("")
}

fun saveAddedStudents(

    context: Context,

    students: List<Student>
) {

    val array =
        JSONArray()

    students.forEach { student ->

        val objectData =
            JSONObject()

        objectData.put(
            "id",
            student.id
        )

        objectData.put(
            "name",
            student.name
        )

        objectData.put(
            "college",
            student.college
        )

        objectData.put(
            "phone",
            student.phone
        )

        objectData.put(
            "group",
            student.group
        )

        objectData.put(
            "imageUri",
            student.imageUri ?: ""
        )

        objectData.put(
            "imageResourceName",
            if (student.imageResource != null) {
                context.resources.getResourceEntryName(student.imageResource)
            } else {
                ""
            }
        )

        objectData.put(
            "color",
            student.color.value.toLong()
        )

        array.put(objectData)
    }

    context
        .getSharedPreferences(
            "student_directory",
            Context.MODE_PRIVATE
        )
        .edit()
        .putString(
            "students",
            array.toString()
        )
        .apply()
}

fun loadSavedStudents(
    context: Context
): List<Student> {

    val preferences =
        context.getSharedPreferences(
            "student_directory",
            Context.MODE_PRIVATE
        )

    val saved =
        preferences.getString(
            "students",
            null
        )
            ?: return emptyList()

    val result =
        mutableListOf<Student>()

    try {

        val array =
            JSONArray(saved)

        for (
        index in
        0 until array.length()
        ) {

            val objectData =
                array.getJSONObject(index)

            val colorValue =
                objectData.getLong("color")

            val imageResourceName = objectData.optString("imageResourceName").trim()
            val imageResource = if (imageResourceName.isNotEmpty()) {
                context.resources.getIdentifier(
                    imageResourceName,
                    "drawable",
                    context.packageName
                ).takeIf { it != 0 }
            } else {
                null
            }

            result.add(

                Student(

                    id =
                        objectData.getString(
                            "id"
                        ),

                    name =
                        objectData.getString(
                            "name"
                        ),

                    college =
                        objectData.getString(
                            "college"
                        ),

                    phone =
                        objectData.getString(
                            "phone"
                        ),

                    group =
                        objectData.getString(
                            "group"
                        ),

                    color =
                        Color(
                            colorValue.toULong()
                        ),

                    imageResource =
                        imageResource,

                    imageUri =
                        objectData.getString(
                            "imageUri"
                        ).ifEmpty {
                            null
                        }
                )
            )
        }

    } catch (_: Exception) {

        return emptyList()
    }

    return result
}

fun hasSavedUniversities(context: Context): Boolean {
    return context.getSharedPreferences(
        "student_directory",
        Context.MODE_PRIVATE
    ).contains("universities")
}

fun saveUniversities(context: Context, universities: List<String>) {
    val array = JSONArray()

    universities
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .distinct()
        .forEach { array.put(it) }

    context.getSharedPreferences(
        "student_directory",
        Context.MODE_PRIVATE
    ).edit()
        .putString("universities", array.toString())
        .apply()
}

fun loadSavedUniversities(context: Context): List<String> {
    val saved = context.getSharedPreferences(
        "student_directory",
        Context.MODE_PRIVATE
    ).getString("universities", null)
        ?: return emptyList()

    return try {
        val array = JSONArray(saved)
        val result = mutableListOf<String>()

        for (index in 0 until array.length()) {
            val value = array.get(index)

            when (value) {
                is JSONObject -> {
                    val name = value.optString("name").trim()
                    if (name.isNotEmpty()) {
                        result.add(name)
                    }
                }

                else -> {
                    val raw = value.toString().trim()
                    if (raw.isEmpty()) continue

                    if (raw.startsWith("{") && raw.endsWith("}")) {
                        try {
                            val objectData = JSONObject(raw)
                            val name = objectData.optString("name").trim()
                            if (name.isNotEmpty()) {
                                result.add(name)
                            }
                        } catch (_: Exception) {
                        }
                    } else {
                        result.add(raw)
                    }
                }
            }
        }

        result
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
    } catch (_: Exception) {
        emptyList()
    }
}

fun hasSavedGroups(context: Context): Boolean {
    return context.getSharedPreferences(
        "student_directory",
        Context.MODE_PRIVATE
    ).contains("groups")
}

fun saveGroups(context: Context, groups: List<GroupInfo>) {
    val array = JSONArray()

    groups
        .distinctBy { "${it.university}::${it.name}" }
        .forEach { group ->
            val objectData = JSONObject()
            objectData.put("university", group.university)
            objectData.put("name", group.name)
            array.put(objectData)
        }

    context.getSharedPreferences(
        "student_directory",
        Context.MODE_PRIVATE
    ).edit()
        .putString("groups", array.toString())
        .apply()
}

fun loadSavedGroups(context: Context): List<GroupInfo> {
    val preferences = context.getSharedPreferences(
        "student_directory",
        Context.MODE_PRIVATE
    )

    val saved = preferences.getString("groups", null)
        ?: return emptyList()

    return try {
        val array = JSONArray(saved)
        val result = mutableListOf<GroupInfo>()

        for (index in 0 until array.length()) {
            val item = array.get(index)

            if (item is JSONObject) {
                val university = item.optString("university").trim()
                val name = item.optString("name").trim()

                if (university.isNotEmpty() && name.isNotEmpty()) {
                    result.add(
                        GroupInfo(
                            university = university,
                            name = name
                        )
                    )
                }
            } else {
                val name = item.toString().trim()
                if (name.isNotEmpty()) {
                    result.add(
                        GroupInfo(
                            university = "KL University",
                            name = name
                        )
                    )
                }
            }
        }

        result.distinctBy { "${it.university}::${it.name}" }
    } catch (_: Exception) {
        emptyList()
    }
}

fun saveDeletedStudentIds(context: Context, ids: Set<String>) {
    val array = JSONArray()
    ids.sorted().forEach { array.put(it) }
    context.getSharedPreferences("student_directory", Context.MODE_PRIVATE)
        .edit().putString("deleted_student_ids", array.toString()).apply()
}

fun loadDeletedStudentIds(context: Context): List<String> {
    val saved = context.getSharedPreferences("student_directory", Context.MODE_PRIVATE)
        .getString("deleted_student_ids", null) ?: return emptyList()
    return try {
        val array = JSONArray(saved)
        (0 until array.length()).mapNotNull { i -> array.optString(i).trim().ifEmpty { null } }.distinct()
    } catch (_: Exception) { emptyList() }
}


fun saveAppTheme(context: Context, mode: AppThemeMode) {
    context.getSharedPreferences("student_directory", Context.MODE_PRIVATE)
        .edit()
        .putString("app_theme", mode.name)
        .apply()
}

fun loadAppTheme(context: Context): AppThemeMode {
    val saved = context.getSharedPreferences("student_directory", Context.MODE_PRIVATE)
        .getString("app_theme", AppThemeMode.OCEAN.name)
        ?: AppThemeMode.OCEAN.name
    return runCatching { AppThemeMode.valueOf(saved) }
        .getOrDefault(AppThemeMode.OCEAN)
}

