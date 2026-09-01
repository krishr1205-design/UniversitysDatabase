package com.example.program1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.program1.data.GroupInfo
import com.example.program1.data.Student
import com.example.program1.ui.common.LocalPhoto
import com.example.program1.ui.common.StudentImage
import com.example.program1.ui.theme.themePrimary

// ─── Add Student Screen ───────────────────────────────────────────────────────

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
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        Text("ADD STUDENT", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = themePrimary())
        Spacer(Modifier.height(8.dp))
        Text("University: $universityName", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text("Group: $groupName", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))

        // Photo preview
        if (photoUri != null) {
            LocalPhoto(uriString = photoUri, modifier = Modifier.size(150.dp))
        } else {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.LightGray, RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("PHOTO", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(15.dp))
        Button(onClick = onChoosePhoto) { Text("CHOOSE PHOTO") }
        Spacer(Modifier.height(20.dp))

        StudentFormFields(
            name = name, id = id, phone = phone,
            onNameChange = onNameChange, onIdChange = onIdChange, onPhoneChange = onPhoneChange
        )

        if (error.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text(text = error, color = Color.Red, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onAdd,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
        ) { Text("ADD STUDENT") }

        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onCancel) { Text("CANCEL") }
        Spacer(Modifier.height(25.dp))
    }
}

// ─── Edit Student Screen ──────────────────────────────────────────────────────

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
    val availableGroups = groups.filter { it.university == university }.map { it.name }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Text("EDIT STUDENT", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = themePrimary())
        Spacer(Modifier.height(20.dp))

        // Photo preview
        if (photoUri != null) LocalPhoto(photoUri, Modifier.size(150.dp))
        else StudentImage(student, 150.dp)

        Spacer(Modifier.height(12.dp))
        Button(onClick = onChoosePhoto) { Text("CHANGE PHOTO") }
        Spacer(Modifier.height(18.dp))

        StudentFormFields(
            name = name, id = id, phone = phone,
            onNameChange = onNameChange, onIdChange = onIdChange, onPhoneChange = onPhoneChange
        )

        Spacer(Modifier.height(12.dp))

        // University dropdown
        Box(Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { showUniversityMenu = true },
                modifier = Modifier.fillMaxWidth()
            ) { Text("University: $university") }
            DropdownMenu(
                expanded = showUniversityMenu,
                onDismissRequest = { showUniversityMenu = false }
            ) {
                universities.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = { university = item; group = ""; showUniversityMenu = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Group dropdown
        Box(Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { showGroupMenu = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = availableGroups.isNotEmpty()
            ) { Text(if (group.isBlank()) "Select Group" else "Group: $group") }
            DropdownMenu(
                expanded = showGroupMenu,
                onDismissRequest = { showGroupMenu = false }
            ) {
                availableGroups.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = { group = item; showGroupMenu = false }
                    )
                }
            }
        }

        if (error.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text(error, color = Color.Red, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { onSave(university, group) },
            colors = ButtonDefaults.buttonColors(containerColor = themePrimary())
        ) { Text("SAVE CHANGES") }

        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onCancel) { Text("CANCEL") }
        Spacer(Modifier.height(25.dp))
    }
}

// ─── Shared Form Fields ───────────────────────────────────────────────────────

@Composable
private fun StudentFormFields(
    name: String,
    id: String,
    phone: String,
    onNameChange: (String) -> Unit,
    onIdChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Student Name") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(
        value = id,
        onValueChange = onIdChange,
        label = { Text("Student ID (6 digits)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(
        value = phone,
        onValueChange = onPhoneChange,
        label = { Text("Phone Number") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    )
}
