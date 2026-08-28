package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JarvisColorScheme = darkColorScheme(
    primary = JarvisCyan,
    onPrimary = JarvisBackgroundDark,
    primaryContainer = JarvisSurfaceElevated,
    onPrimaryContainer = JarvisCyanBright,
    secondary = JarvisAmber,
    onSecondary = JarvisBackgroundDark,
    secondaryContainer = JarvisSurfaceDark,
    onSecondaryContainer = JarvisAmber,
    tertiary = JarvisGold,
    onTertiary = JarvisBackgroundDark,
    background = JarvisBackgroundDark,
    onBackground = JarvisTextPrimary,
    surface = JarvisSurfaceDark,
    onSurface = JarvisTextPrimary,
    surfaceVariant = JarvisSurfaceElevated,
    onSurfaceVariant = JarvisTextSecondary,
    outline = JarvisSurfaceBorder,
    error = JarvisAlertRed,
    onError = JarvisTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Preserve iconic JARVIS sci-fi theme
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = JarvisColorScheme,
        typography = Typography,
        content = content
    )
}
