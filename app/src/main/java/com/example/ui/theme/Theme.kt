package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MizenPrimary,
    onPrimary = Color.White,
    primaryContainer = MizenPrimaryContainer,
    onPrimaryContainer = MizenOnPrimaryContainer,
    secondary = MizenGold,
    onSecondary = Color.White,
    secondaryContainer = MizenGoldContainer,
    onSecondaryContainer = MizenOnGoldContainer,
    tertiary = MizenIslamicGreen,
    onTertiary = Color.White,
    background = MizenBackgroundLight,
    onBackground = MizenTextPrimary,
    surface = MizenSurfaceLight,
    onSurface = MizenTextPrimary,
    surfaceVariant = MizenSurfaceVariant,
    onSurfaceVariant = MizenTextSecondary,
    outline = MizenBorderLight,
    error = MizenError,
    onError = Color.White,
    errorContainer = MizenErrorContainer,
    onErrorContainer = MizenError
)

private val DarkColorScheme = darkColorScheme(
    primary = MizenPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = MizenPrimaryDark,
    onPrimaryContainer = MizenPrimaryContainer,
    secondary = MizenGoldLight,
    onSecondary = Color.Black,
    secondaryContainer = MizenSurfaceDarkVariant,
    onSecondaryContainer = MizenGoldLight,
    tertiary = MizenIslamicGreen,
    onTertiary = Color.White,
    background = MizenBackgroundDark,
    onBackground = MizenTextPrimaryDark,
    surface = MizenSurfaceDark,
    onSurface = MizenTextPrimaryDark,
    surfaceVariant = MizenSurfaceDarkVariant,
    onSurfaceVariant = MizenTextSecondaryDark,
    outline = MizenBorderDark,
    error = MizenError,
    onError = Color.White,
    errorContainer = MizenErrorContainer,
    onErrorContainer = MizenError
)

@Composable
fun MizenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = MizenTheme(darkTheme, content)
