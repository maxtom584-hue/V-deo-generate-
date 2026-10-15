package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AnimePrimaryDark,
    onPrimary = AnimeOnPrimaryDark,
    primaryContainer = AnimePrimaryContainerDark,
    onPrimaryContainer = AnimeOnPrimaryContainerDark,
    secondary = AnimeSecondaryDark,
    onSecondary = AnimeOnSecondaryDark,
    secondaryContainer = AnimeSecondaryContainerDark,
    onSecondaryContainer = AnimeOnSecondaryContainerDark,
    tertiary = AnimeTertiaryDark,
    onTertiary = AnimeOnTertiaryDark,
    tertiaryContainer = AnimeTertiaryContainerDark,
    onTertiaryContainer = AnimeOnTertiaryContainerDark,
    background = AnimeBackgroundDark,
    surface = AnimeSurfaceDark,
    surfaceVariant = AnimeSurfaceVariantDark,
    onBackground = AnimeOnBackgroundDark,
    onSurface = AnimeOnSurfaceDark,
    onSurfaceVariant = AnimeOnSurfaceVariantDark,
    outline = AnimeOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = AnimePrimaryLight,
    onPrimary = AnimeOnPrimaryLight,
    primaryContainer = AnimePrimaryContainerLight,
    onPrimaryContainer = AnimeOnPrimaryContainerLight,
    secondary = AnimeSecondaryLight,
    onSecondary = AnimeOnSecondaryLight,
    secondaryContainer = AnimeSecondaryContainerLight,
    onSecondaryContainer = AnimeOnSecondaryContainerLight,
    tertiary = AnimeTertiaryLight,
    onTertiary = AnimeOnTertiaryLight,
    tertiaryContainer = AnimeTertiaryContainerLight,
    onTertiaryContainer = AnimeOnTertiaryContainerLight,
    background = AnimeBackgroundLight,
    surface = AnimeSurfaceLight,
    surfaceVariant = AnimeSurfaceVariantLight,
    onBackground = AnimeOnBackgroundLight,
    onSurface = AnimeOnSurfaceLight,
    onSurfaceVariant = AnimeOnSurfaceVariantLight,
    outline = AnimeOutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Default to vibrant custom anime palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
