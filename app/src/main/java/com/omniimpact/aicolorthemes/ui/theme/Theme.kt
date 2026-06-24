package com.omniimpact.aicolorthemes.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LightGrayText,
    secondary = DarkGrayLine,
    background = Black,
    surface = Black,
    onBackground = LightGrayText,
    onSurface = LightGrayText,
    outline = DarkGrayLine
)

private val LightColorScheme = lightColorScheme(
    primary = VeryDarkGray,
    secondary = GrayLine,
    background = LightGray,
    surface = LightGray,
    onBackground = VeryDarkGray,
    onSurface = VeryDarkGray,
    outline = GrayLine
)

@Composable
fun AIColorThemesTheme(
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
