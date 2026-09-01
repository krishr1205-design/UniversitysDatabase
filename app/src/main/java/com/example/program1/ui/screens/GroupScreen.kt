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

// ─── Group List Screen ────────────────────────────────────────────────────────

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        Text("SELECT GROUP", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = themePrimary())
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
                    color = cardPalette[index % cardPalette.size],
                    canDelete = true,
                    onClick = { onGroupSelected(group) },
                    onEdit = { newName -> onEditGroup(group, newName) },
                    onDelete = { onDeleteGroup(group) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }

        Button(
            onClick = { groupName = ""; showAddDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = themePrimary())
        ) {
            Text("+ ADD GROUP")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
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
                Button(onClick = {
                    val clean = groupName.trim()
                    if (clean.isNotEmpty()) { onAddGroup(clean); showAddDialog = false }
                }) { Text("ADD") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddDialog = false }) { Text("CANCEL") }
            }
        )
    }
}

// ─── Group Row ────────────────────────────────────────────────────────────────

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

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Card(
            modifier = Modifier.weight(1f).height(70.dp).clickable(onClick = onClick),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = color)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
            ) { Text("DELETE", color = Color(0xFFC62828)) }
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
                Text("Delete \"$text\" and all students in this group?\n\nThis action cannot be undone.")
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
