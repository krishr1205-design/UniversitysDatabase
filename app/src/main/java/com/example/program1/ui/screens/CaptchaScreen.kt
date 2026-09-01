package com.example.program1.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.program1.ui.theme.themeMuted
import com.example.program1.ui.theme.themePrimary
import com.example.program1.ui.theme.themeSurface
import kotlin.random.Random

// ─── CAPTCHA Screen ───────────────────────────────────────────────────────────

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
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CAPTCHA",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = themePrimary()
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Security Verification",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.width(280.dp).height(110.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = themeSurface())
        ) {
            CaptchaImage(captchaCode = captchaCode)
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(onClick = onRefresh) {
            Text("↻ New CAPTCHA")
        }

        Spacer(Modifier.height(18.dp))

        OutlinedTextField(
            value = captchaInput,
            onValueChange = onInputChange,
            label = { Text("Enter CAPTCHA") },
            singleLine = true
        )

        if (captchaError) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Incorrect CAPTCHA. Try again.",
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(20.dp))

        Button(onClick = onVerify) {
            Text("VERIFY")
        }
    }
}

// ─── CAPTCHA Canvas ───────────────────────────────────────────────────────────

@Composable
fun CaptchaImage(captchaCode: String) {
    Canvas(modifier = Modifier.fillMaxSize().padding(5.dp)) {
        // Background
        drawRect(color = Color(0xFFF7F7F7))

        // Noise dots
        repeat(180) {
            drawCircle(
                color = Color(
                    red = Random.nextInt(80, 200),
                    green = Random.nextInt(80, 200),
                    blue = Random.nextInt(80, 200)
                ),
                radius = Random.nextFloat() * 2.5f + 0.5f,
                center = Offset(Random.nextFloat() * size.width, Random.nextFloat() * size.height)
            )
        }

        // Distractor lines
        repeat(6) {
            drawLine(
                color = Color(0xFF78909C),
                start = Offset(Random.nextFloat() * size.width, Random.nextFloat() * size.height),
                end = Offset(Random.nextFloat() * size.width, Random.nextFloat() * size.height),
                strokeWidth = 1.5f
            )
        }

        // Characters
        val charWidth = size.width / (captchaCode.length + 1)
        captchaCode.forEachIndexed { index, char ->
            val x = charWidth * (index + 1)
            val y = size.height / 2f + 17.dp.toPx()
            val rotation = Random.nextFloat() * 40f - 20f
            rotate(degrees = rotation, pivot = Offset(x, y)) {
                drawContext.canvas.nativeCanvas.drawText(
                    char.toString(), x, y,
                    android.graphics.Paint().apply {
                        isAntiAlias = true
                        textSize = 42.dp.toPx()
                        typeface = android.graphics.Typeface.create(
                            android.graphics.Typeface.SERIF,
                            android.graphics.Typeface.BOLD
                        )
                        color = android.graphics.Color.BLACK
                    }
                )
            }
        }

        // Horizontal noise lines
        repeat(3) {
            val y = Random.nextFloat() * size.height
            drawLine(
                color = Color(0xFF9E9E9E),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
        }
    }
}

// ─── CAPTCHA Generator ────────────────────────────────────────────────────────

/** Generates a 5-character alphanumeric CAPTCHA string (no ambiguous chars). */
fun generateCaptcha(): String {
    val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    return (1..5).map { chars[Random.nextInt(chars.length)] }.joinToString("")
}
