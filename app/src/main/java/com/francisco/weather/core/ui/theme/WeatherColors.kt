package com.francisco.weather.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class WeatherColors(
    // Semantic surfaces
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val error: Color,
    // Glass overlay layer (drawn on sky gradient)
    val glassFill: Color,
    val glassStroke: Color,
    val glassStrong: Color,
    // Content color on sky gradient / glass surfaces
    val onSky: Color,
)

val LightWeatherColors = WeatherColors(
    background    = WeatherBackground,
    surface       = WeatherSurface,
    onSurface     = Color(0xFF1A1C1E),
    textPrimary   = Color(0xFF1A1C1E),
    textSecondary = Color(0xFF44474E),
    accent        = SkyBlue,
    error         = ErrorColor,
    glassFill     = Color(0x1FFFFFFF),
    glassStroke   = Color(0x45FFFFFF),
    glassStrong   = Color(0x30FFFFFF),
    onSky         = Color(0xFFFFFFFF),
)

val DarkWeatherColors = WeatherColors(
    background    = BackgroundDark,
    surface       = SurfaceDark,
    onSurface     = Color(0xFFE2E2E6),
    textPrimary   = Color(0xFFE2E2E6),
    textSecondary = Color(0xFFB7BBC4),
    accent        = SkyBlueDarkTheme,
    error         = ErrorDark,
    glassFill     = Color(0x1FFFFFFF),
    glassStroke   = Color(0x45FFFFFF),
    glassStrong   = Color(0x30FFFFFF),
    onSky         = Color(0xFFFFFFFF),
)

val LocalWeatherColors = staticCompositionLocalOf { LightWeatherColors }
