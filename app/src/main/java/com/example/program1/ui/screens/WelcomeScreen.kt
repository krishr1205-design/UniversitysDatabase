package com.example.program1.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.program1.ui.theme.AppThemeMode
import com.example.program1.ui.theme.themeMuted
import com.example.program1.ui.theme.themeOnPrimary
import com.example.program1.ui.theme.themePrimary

// ─── Welcome Screen ───────────────────────────────────────────────────────────

@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
    onTheme: () -> Unit,
    themeName: String
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "UNIVERSITY'S DATABASES",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = themePrimary()
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "CLASSMATE DIRECTORY",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "WELCOME",
            fontSize = 22.sp,
            color = themeMuted()
        )

        Spacer(Modifier.height(30.dp))

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

// ─── Theme Picker Dialog ──────────────────────────────────────────────────────

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
                AppThemeMode.entries.forEach { mode ->
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
