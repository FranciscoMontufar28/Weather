package com.francisco.weather.feature.forecast.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import com.francisco.weather.core.ui.sky.computeSkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.forecast.domain.model.Astro
import com.francisco.weather.feature.forecast.domain.model.Condition
import com.francisco.weather.feature.forecast.domain.model.CurrentWeather
import com.francisco.weather.feature.forecast.domain.model.DayWeather
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import com.francisco.weather.feature.forecast.domain.model.HourWeather
import com.francisco.weather.feature.forecast.presentation.composables.screens.ForecastLandscape
import com.francisco.weather.feature.forecast.presentation.composables.screens.ForecastPortrait

private val PREVIEW_ASTRO = Astro("05:47 AM", "06:10 PM", "Waxing Gibbous", 93)

private val PREVIEW_HOURS = listOf(
    HourWeather("2025-06-25 09:00", 17.0, Condition("Partly cloudy", "https://cdn.weatherapi.com/weather/64x64/day/116.png", 1003), 10, true),
    HourWeather("2025-06-25 12:00", 20.0, Condition("Partly cloudy", "https://cdn.weatherapi.com/weather/64x64/day/116.png", 1003), 5, true),
    HourWeather("2025-06-25 15:00", 22.0, Condition("Sunny", "https://cdn.weatherapi.com/weather/64x64/day/113.png", 1000), 0, true),
    HourWeather("2025-06-25 18:00", 19.0, Condition("Partly cloudy", "https://cdn.weatherapi.com/weather/64x64/day/116.png", 1003), 15, false),
)

private val PREVIEW_FORECAST = ForecastData(
    locationName = "London",
    region = "City of London",
    country = "United Kingdom",
    days = listOf(
        DayWeather("2025-06-25", 19.0, 23.0, 15.0, Condition("Partly cloudy", "https://cdn.weatherapi.com/weather/64x64/day/116.png", 1003), uv = 5.0, chanceOfRain = 20, astro = PREVIEW_ASTRO, hours = PREVIEW_HOURS),
        DayWeather("2025-06-26", 17.0, 20.0, 13.0, Condition("Light rain", "https://cdn.weatherapi.com/weather/64x64/day/296.png", 1183), uv = 2.0, chanceOfRain = 80, astro = PREVIEW_ASTRO),
        DayWeather("2025-06-27", 22.0, 26.0, 18.0, Condition("Sunny", "https://cdn.weatherapi.com/weather/64x64/day/113.png", 1000), uv = 8.0, astro = PREVIEW_ASTRO),
    ),
    current = CurrentWeather(
        tempC = 21.0,
        condition = Condition("Partly cloudy", "https://cdn.weatherapi.com/weather/64x64/day/116.png", 1003),
        humidity = 68,
        windKph = 12.0,
        feelsLikeC = 20.0,
        uv = 5.0,
    ),
)

@Preview(name = "Portrait · Day", showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PortraitDayPreview() {
    val sky = computeSkyColors(13f)
    WeatherTheme {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(0f to sky.top, 0.55f to sky.mid, 1f to sky.bottom))) {
            ForecastPortrait(forecast = PREVIEW_FORECAST, sky = sky)
        }
    }
}

@Preview(name = "Landscape · Dusk", showBackground = true, widthDp = 917, heightDp = 412)
@Composable
private fun LandscapeDuskPreview() {
    val sky = computeSkyColors(19.5f)
    WeatherTheme {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(0f to sky.top, 0.55f to sky.mid, 1f to sky.bottom))) {
            ForecastLandscape(forecast = PREVIEW_FORECAST, sky = sky, onBack = {})
        }
    }
}
