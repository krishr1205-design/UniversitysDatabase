package com.example.program1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.program1.data.Student
import com.example.program1.ui.common.StudentImage
import com.example.program1.ui.theme.themeMuted
import com.example.program1.ui.theme.themeOnPrimary
import com.example.program1.ui.theme.themePrimary
import com.example.program1.ui.theme.themeSurface

// ─── Student Details Screen ───────────────────────────────────────────────────

@Composable
fun StudentDetailsScreen(
    student: Student,
    onEdit: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(student.color)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = themeSurface())
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StudentImage(student = student, size = 160.dp)

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "STUDENT INFORMATION",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )

                Spacer(Modifier.height(25.dp))

                DetailItem(title = "Full Name", value = student.name)
                Spacer(Modifier.height(12.dp))
                DetailItem(title = "Student ID", value = student.id)
                Spacer(Modifier.height(12.dp))
                DetailItem(title = "Group", value = student.group)
                Spacer(Modifier.height(12.dp))
                DetailItem(title = "University", value = student.college)
                Spacer(Modifier.height(12.dp))
                DetailItem(title = "Contact Number", value = student.phone)

                Spacer(Modifier.height(25.dp))

                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = themePrimary())
                ) {
                    Text("EDIT DATA", color = themeOnPrimary())
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(onClick = onBack) { Text("BACK") }
            }
        }
    }
}

// ─── Detail Item ──────────────────────────────────────────────────────────────

@Composable
fun DetailItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = themeMuted()
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
