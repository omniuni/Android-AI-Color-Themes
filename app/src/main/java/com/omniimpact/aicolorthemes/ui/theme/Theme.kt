package com.omniimpact.aicolorthemes.ui.theme

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
    themeKey: String? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when (themeKey) {
        "m3_light" -> lightColorScheme()
        "m3_dark" -> darkColorScheme()
        "dynamic_light" -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                dynamicLightColorScheme(context)
            } else {
                lightColorScheme()
            }
        }
        "dynamic_dark" -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                dynamicDarkColorScheme(context)
            } else {
                darkColorScheme()
            }
        }
        "light" -> LightColorScheme
        "dark" -> DarkColorScheme
        else -> if (darkTheme) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
