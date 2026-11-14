package com.example.restoria.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RestoriaColorScheme = darkColorScheme(
    primary = Turquoise,
    onPrimary = BackgroundBottom,
    secondary = Blue,
    onSecondary = TextPrimary,
    tertiary = Orange,
    background = BackgroundBottom,
    onBackground = TextPrimary,
    surface = SurfaceColor,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantColor,
    onSurfaceVariant = TextMuted,
    outline = OutlineColor,
    error = ErrorColor,
)

@Composable
fun RestoriaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RestoriaColorScheme,
        typography = RestoriaTypography,
        content = content
    )
}