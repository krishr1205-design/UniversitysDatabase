package com.example.program1.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ─── Theme Modes ─────────────────────────────────────────────────────────────

enum class AppThemeMode(val title: String) {
    OCEAN("Ocean Blue"),
    DARK("Dark"),
    FOREST("Forest Green"),
    PURPLE("Royal Purple"),
    SUNSET("Sunset Orange")
}

// ─── Theme Color Data ─────────────────────────────────────────────────────────

data class AppThemeColors(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val text: Color,
    val muted: Color,
    val onPrimary: Color
)

val LocalAppTheme = staticCompositionLocalOf {
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

// ─── Convenience Composable Accessors ────────────────────────────────────────

@Composable fun themePrimary(): Color = LocalAppTheme.current.primary
@Composable fun themeBackground(): Color = LocalAppTheme.current.background
@Composable fun themeSurface(): Color = LocalAppTheme.current.surface
@Composable fun themeText(): Color = LocalAppTheme.current.text
@Composable fun themeMuted(): Color = LocalAppTheme.current.muted
@Composable fun themeOnPrimary(): Color = LocalAppTheme.current.onPrimary

// ─── Shared card palette ──────────────────────────────────────────────────────

val cardPalette = listOf(
    Color(0xFFFFCC80),
    Color(0xFFB3E5FC),
    Color(0xFFFFCDD2),
    Color(0xFFC8E6C9),
    Color(0xFFD1C4E9),
    Color(0xFFFFF9C4),
    Color(0xFFFFCCBC),
    Color(0xFFB2DFDB)
)

/** Pastel palette used when assigning a color to a newly-created student card. */
val studentCardPalette = listOf(
    Color(0xFFFFE0B2),
    Color(0xFFB2EBF2),
    Color(0xFFC5E1A5),
    Color(0xFFD1C4E9),
    Color(0xFFFFCDD2),
    Color(0xFFFFF9C4),
    Color(0xFFB3E5FC),
    Color(0xFFFFCCBC)
)
