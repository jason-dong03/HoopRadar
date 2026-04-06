package com.main.hoopradar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HoopRadarColorScheme = darkColorScheme(
    primary = HoopOrange,
    onPrimary = Color.White,
    primaryContainer = HoopOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = HoopOrangeLight,
    onSecondary = Color.White,
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkElevated,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun HoopRadarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HoopRadarColorScheme,
        typography = Typography,
        content = content
    )
}
