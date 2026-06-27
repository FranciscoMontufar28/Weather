package com.francisco.weather.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

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

    val weatherColors = if (darkTheme) DarkWeatherColors else LightWeatherColors

    CompositionLocalProvider(LocalWeatherColors provides weatherColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = WeatherTypography,
            content = content,
        )
    }
}

/**
 * Design token accessor — mirrors [MaterialTheme].
 * Static tokens ([Size], [Radius], [Border], [IconSize]) are plain [Dp], usable anywhere.
 * [Colors] is a @Composable getter — only readable inside composable scope.
 *
 * Usage:
 *   WeatherTheme.Size.large
 *   WeatherTheme.Radius.medium
 *   WeatherTheme.Colors.glassFill
 */
object WeatherTheme {

    /** Current [WeatherColors] from the nearest [LocalWeatherColors]. */
    val Colors: WeatherColors
        @Composable @ReadOnlyComposable
        get() = LocalWeatherColors.current

    /** Spacing / padding / gap scale. */
    object Size {
        val none    = 0.dp
        val xSmall  = 4.dp
        val small   = 6.dp
        val xMedium = 8.dp
        val medium  = 12.dp
        val large   = 16.dp
        val xLarge  = 20.dp
        val huge    = 24.dp
        val xHuge   = 32.dp
    }

    /** Corner radius scale. */
    object Radius {
        val small  = 14.dp
        val medium = 20.dp
        val large  = 32.dp
        val full   = 50.dp
    }

    /** Stroke / divider width. */
    object Border {
        val hairline = 0.5.dp
        val thin     = 1.dp
    }

    /** Icon and weather-glyph sizes. */
    object IconSize {
        val small        = 16.dp
        val medium       = 20.dp
        val large        = 28.dp
        val weatherSmall = 44.dp
        val weather      = 60.dp
    }
}
