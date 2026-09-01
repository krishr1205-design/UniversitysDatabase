package com.example.program1.data

import androidx.compose.ui.graphics.Color

/**
 * Represents a student in the directory.
 *
 * @param id          6-digit unique student ID
 * @param name        Full name of the student
 * @param college     University the student belongs to
 * @param phone       Contact phone number
 * @param group       Group/class the student belongs to
 * @param color       Accent color for the student card
 * @param imageResource Drawable resource ID for bundled demo images
 * @param imageUri    Content URI string for user-picked photos
 */
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

/**
 * Associates a group name with its parent university.
 */
data class GroupInfo(
    val university: String,
    val name: String
)
