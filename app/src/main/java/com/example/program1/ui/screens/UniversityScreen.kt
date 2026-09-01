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
import com.example.program1.ui.theme.cardPalette
import com.example.program1.ui.theme.themeMuted
import com.example.program1.ui.theme.themePrimary
import com.example.program1.ui.theme.themeSurface

// ─── University List Screen ───────────────────────────────────────────────────

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        Text("SELECT UNIVERSITY", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = themePrimary())
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
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No universities added yet.", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("Add a university to start creating groups and students.", color = themeMuted())
                }
            }
            Spacer(Modifier.height(12.dp))
        } else {
            universities.forEachIndexed { index, university ->
                UniversityRow(
                    text = university,
                    color = cardPalette[index % cardPalette.size],
                    onClick = { onUniversitySelected(university) },
                    onEdit = { newName -> onEditUniversity(university, newName) },
                    onDelete = { onDeleteUniversity(university) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }

        Button(
            onClick = { universityName = ""; showAddDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = themePrimary())
        ) {
            Text("+ ADD UNIVERSITY")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
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
                Button(onClick = {
                    val clean = universityName.trim()
                    if (clean.isNotEmpty()) { onAddUniversity(clean); showAddDialog = false }
                }) { Text("ADD") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddDialog = false }) { Text("CANCEL") }
            }
        )
    }
}

// ─── University Row ───────────────────────────────────────────────────────────

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

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Card(
            modifier = Modifier.weight(1f).height(70.dp).clickable(onClick = onClick),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = color)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text, fontSize = 19.sp, fontWeight = FontWeight.Bold)
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
        ) { Text("DELETE", color = Color(0xFFC62828)) }
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
