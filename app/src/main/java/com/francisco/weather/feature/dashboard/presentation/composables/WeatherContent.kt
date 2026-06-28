package com.francisco.weather.feature.dashboard.presentation.composables

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.francisco.weather.R
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.sky.computeSkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.forecast.domain.model.Condition
import com.francisco.weather.feature.forecast.domain.model.CurrentWeather
import com.francisco.weather.feature.forecast.domain.model.DayWeather
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import kotlin.math.roundToInt

@Composable
internal fun WeatherContent(
    forecast: ForecastData,
    sky: SkyColors,
    context: Context,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xSmall),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.small),
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = sky.accent,
                modifier = Modifier.size(WeatherTheme.IconSize.small),
            )
            Text(
                text = stringResource(R.string.location_my_location),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = sky.textMuted,
            )
        }
        Spacer(Modifier.height(WeatherTheme.Size.xSmall))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xSmall)) {
                Text(
                    text = forecast.locationName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = WeatherTheme.Colors.onSky,
                )
                Text(
                    text = forecast.country,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = sky.textMuted,
                )
                Text(
                    text = forecast.current.condition.text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WeatherTheme.Colors.onSky,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xSmall),
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(forecast.current.condition.iconUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = forecast.current.condition.text,
                    modifier = Modifier.size(WeatherTheme.IconSize.weather),
                )
                Text(
                    text = "${forecast.current.tempC.roundToInt()}°",
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Bold,
                    color = WeatherTheme.Colors.onSky,
                )
            }
        }
    }
}

@Preview(name = "Day", showBackground = true, widthDp = 412, backgroundColor = 0xFF245CA8)
@Composable
private fun PreviewWeatherContent() {
    WeatherTheme {
        WeatherContent(
            forecast = ForecastData(
                locationName = "Bogotá",
                region = "Bogota D.C.",
                country = "Colombia",
                days = listOf(
                    DayWeather(
                        date = "2025-06-28",
                        avgTempC = 14.0,
                        maxTempC = 17.0,
                        minTempC = 10.0,
                        condition = Condition("Partly cloudy", "https://cdn.weatherapi.com/weather/64x64/day/116.png", 1003),
                    )
                ),
                current = CurrentWeather(
                    tempC = 15.0,
                    condition = Condition("Partly cloudy", "https://cdn.weatherapi.com/weather/64x64/day/116.png", 1003),
                    humidity = 72,
                    windKph = 10.0,
                    feelsLikeC = 14.0,
                ),
            ),
            sky = computeSkyColors(13f),
            context = LocalContext.current,
            onClick = {},
        )
    }
}