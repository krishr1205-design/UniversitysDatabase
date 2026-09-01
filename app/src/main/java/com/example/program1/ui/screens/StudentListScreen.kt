package com.example.program1.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.program1.data.Student
import com.example.program1.ui.common.StudentImage
import com.example.program1.ui.theme.themeMuted
import com.example.program1.ui.theme.themePrimary
import com.example.program1.ui.theme.themeSurface

// ─── Student List Screen ──────────────────────────────────────────────────────

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
        Text(
            "${students.size} student${if (students.size == 1) "" else "s"}",
            color = themeMuted()
        )

        Spacer(Modifier.height(18.dp))

        if (students.isEmpty()) {
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = themeSurface())
            ) {
                Text(
                    "No students in this group yet.",
                    modifier = Modifier.padding(20.dp),
                    color = themeMuted()
                )
            }
            Spacer(Modifier.height(12.dp))
        } else {
            students.forEach { student ->
                StudentCard(
                    student = student,
                    onClick = { onStudentClick(student) },
                    onEdit = { onEditStudent(student) },
                    onDelete = { onDeleteStudent(student) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }

        Button(
            onClick = onAddStudent,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            modifier = Modifier.fillMaxWidth()
        ) { Text("+ ADD STUDENT") }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("BACK TO GROUPS")
        }

        Spacer(Modifier.height(20.dp))
    }
}

// ─── Student Card ─────────────────────────────────────────────────────────────

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
            modifier = Modifier
                .weight(1f)
                .height(82.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = student.color)
        ) {
            Row(
                Modifier.fillMaxSize().padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) { Text("CANCEL") }
            }
        )
    }
}
