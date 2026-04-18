package com.example.chipotlenutritionapp.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Premium Brand Colors
val ChipotleRed = Color(0xFF8C1515)
val FreshGreen = Color(0xFF43652D)
val DarkCharcoal = Color(0xFF1E1E1E)
val LightCream = Color(0xFFF9F7F1)
val SpiceOrange = Color(0xFFD65A20)

val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = (-1).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)

val LightColors = lightColorScheme(
    primary = ChipotleRed,
    onPrimary = Color.White,
    secondary = FreshGreen,
    onSecondary = Color.White,
    tertiary = SpiceOrange,
    background = LightCream,
    surface = Color.White,
    surfaceVariant = Color(0xFFEBE9E1),
    onSurface = DarkCharcoal
)

val DarkColors = darkColorScheme(
    primary = Color(0xFFB03333),
    onPrimary = Color.White,
    secondary = Color(0xFF5A8A3E),
    onSecondary = Color.White,
    tertiary = Color(0xFFE87741),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurface = Color(0xFFEAEAEA)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
