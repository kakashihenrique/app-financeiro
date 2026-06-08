package com.fincalc.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = Color(0xFF0F4C5C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7E7EC),
    onPrimaryContainer = Color(0xFF002A33),
    secondary = Color(0xFFB86F3B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0C9),
    onSecondaryContainer = Color(0xFF3A1E08),
    tertiary = Color(0xFF3E6B3D),
    onTertiary = Color.White,
    background = Color(0xFFF7F4EE),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFEDEAE2),
    onSurfaceVariant = Color(0xFF454545),
    error = Color(0xFFB3261E),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8FD0DC),
    onPrimary = Color(0xFF002830),
    primaryContainer = Color(0xFF1F4B57),
    onPrimaryContainer = Color(0xFFD7E7EC),
    secondary = Color(0xFFE0B58A),
    onSecondary = Color(0xFF3A1E08),
    secondaryContainer = Color(0xFF5A3A1E),
    onSecondaryContainer = Color(0xFFFFE0C9),
    tertiary = Color(0xFFA6CDA1),
    onTertiary = Color(0xFF103412),
    background = Color(0xFF111315),
    onBackground = Color(0xFFE6E3DD),
    surface = Color(0xFF1A1D20),
    onSurface = Color(0xFFE6E3DD),
    surfaceVariant = Color(0xFF2A2D31),
    onSurfaceVariant = Color(0xFFCAC6BD),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410)
)

private val AppTypography = Typography(
    displaySmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 34.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp)
)

@Composable
fun FinCalcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val scheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(
        colorScheme = scheme,
        typography = AppTypography,
        content = content
    )
}
