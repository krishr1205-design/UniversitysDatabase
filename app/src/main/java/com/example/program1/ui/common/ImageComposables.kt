package com.example.program1.ui.common

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.program1.data.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Displays the correct image for a student:
 * - a user-picked URI photo if available,
 * - else a bundled drawable resource,
 * - else nothing (caller handles the placeholder if needed).
 */
@Composable
fun StudentImage(student: Student, size: Dp) {
    when {
        student.imageUri != null ->
            LocalPhoto(uriString = student.imageUri, modifier = Modifier.size(size))
        student.imageResource != null ->
            Image(
                painter = painterResource(id = student.imageResource),
                contentDescription = "Student photo",
                modifier = Modifier.size(size),
                contentScale = ContentScale.Crop
            )
    }
}

/**
 * Loads and displays an image from a content URI asynchronously.
 * Shows a light-gray placeholder while loading or on failure.
 */
@Composable
fun LocalPhoto(uriString: String, modifier: Modifier) {
    val context = LocalContext.current
    var bitmap by remember(uriString) {
        mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null)
    }

    LaunchedEffect(uriString) {
        bitmap = withContext(Dispatchers.IO) {
            try {
                context.contentResolver
                    .openInputStream(Uri.parse(uriString))
                    ?.use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
            } catch (_: Exception) {
                null
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            contentDescription = "Student photo",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier.background(Color.LightGray, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Photo")
        }
    }
}
