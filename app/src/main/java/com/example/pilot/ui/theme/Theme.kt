package com.example.pilot.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = PrimaryLight,
    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = Color(0xFFFFCDD2),
    tertiary = Tertiary,
    tertiaryContainer = TertiaryContainer,
    background = DarkBackground,
    onBackground = Color(0xFFE8E8E8),
    surface = DarkSurface,
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFAAAAAA),
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    surfaceBright = Color(0xFF222222),
    inverseSurface = Color(0xFFE8E8E8),
    inverseOnSurface = Color(0xFF111111),
    error = Error,
    onError = Color.White,
    outline = Color(0xFF333333),
    outlineVariant = Color(0xFF222222),
)

@Composable
fun PilotTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
