package com.example.chipotlenutritionapp.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryColor = Color(0xFF2E7D32)
val SecondaryColor = Color(0xFFD84315)

// Light Theme
val LightColors = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.White,
    secondary = SecondaryColor,
    onSecondary = Color.White,
    background = Color(0xFFFBFDF8),
    surface = Color.White,
    surfaceVariant = Color(0xFFE8F5E9)
)

// Dark Theme
val DarkColors = darkColorScheme(
    primary = Color(0xFFA5D6A7),
    onPrimary = Color(0xFF1B5E20),
    secondary = Color(0xFFFFAB91),
    onSecondary = Color(0xFFBF360C),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2C2C2C)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
