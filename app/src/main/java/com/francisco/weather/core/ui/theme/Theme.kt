package com.francisco.weather.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = SkyBlueContainer,
    onPrimaryContainer = SkyBlueDark,
    secondary = SunYellow,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = SunYellowContainer,
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF4A2800),
    background = WeatherBackground,
    onBackground = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    surface = WeatherSurface,
    onSurface = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    error = ErrorColor,
    onError = androidx.compose.ui.graphics.Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlueDarkTheme,
    onPrimary = SkyBlueDark,
    primaryContainer = SkyBlueDarkContainer,
    onPrimaryContainer = SkyBlueContainer,
    secondary = SunYellow,
    onSecondary = androidx.compose.ui.graphics.Color(0xFF4A2800),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF6A3C00),
    onSecondaryContainer = SunYellowContainer,
    background = BackgroundDark,
    onBackground = androidx.compose.ui.graphics.Color(0xFFE2E2E6),
    surface = SurfaceDark,
    onSurface = androidx.compose.ui.graphics.Color(0xFFE2E2E6),
    error = ErrorDark,
    onError = androidx.compose.ui.graphics.Color(0xFF690020),
)

@Composable
fun WeatherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
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
        typography = WeatherTypography,
        content = content,
    )
}
