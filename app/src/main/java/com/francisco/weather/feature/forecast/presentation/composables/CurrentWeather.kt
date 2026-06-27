package com.francisco.weather.feature.forecast.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.forecast.domain.model.CurrentWeather
import com.francisco.weather.feature.forecast.domain.model.ForecastData

@Composable
internal fun CurrentWeatherCard(
    forecast: ForecastData,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    val today = forecast.days.firstOrNull()
    val current = forecast.current

    GlassCard(fill = WeatherTheme.Colors.glassStrong, shape = RoundedCornerShape(WeatherTheme.Radius.large), modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = WeatherTheme.Size.xLarge, vertical = WeatherTheme.Size.large),
        ) {
            Text(
                text = forecast.locationName,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = WeatherTheme.Colors.onSky,
            )
            Text(
                text = buildString {
                    if (forecast.region.isNotBlank()) append("${forecast.region}, ")
                    append(forecast.country)
                },
                fontSize = 12.sp,
                color = sky.textMuted,
            )

            Spacer(Modifier.height(WeatherTheme.Size.small))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(current.condition.iconUrl).crossfade(true).build(),
                    contentDescription = current.condition.text,
                    modifier = Modifier.size(WeatherTheme.IconSize.weather),
                )
                Spacer(Modifier.width(WeatherTheme.Size.xMedium))
                Text(
                    text = "${current.tempC.toInt()}°",
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Bold,
                    color = WeatherTheme.Colors.onSky,
                )
            }

            Spacer(Modifier.height(WeatherTheme.Size.xSmall))
            Text(
                text = current.condition.text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = WeatherTheme.Colors.onSky,
            )

            if (today != null) {
                Spacer(Modifier.height(WeatherTheme.Size.xSmall))
                Text(
                    text = "Average ${today.avgTempC.toInt()}°C  ·  H:${today.maxTempC.toInt()}°  L:${today.minTempC.toInt()}°",
                    fontSize = 12.sp,
                    color = sky.textMuted,
                )
            }
        }
    }
}

@Composable
internal fun MetricsBar(
    current: CurrentWeather,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    GlassCard(shape = RoundedCornerShape(WeatherTheme.Radius.medium), modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(WeatherTheme.Size.medium),
        ) {
            MetricItem(icon = Icons.Default.WaterDrop, value = "${current.humidity}%", label = "Humidity", sky = sky)
            MetricItem(icon = Icons.Default.Air, value = "${current.windKph.toInt()} km/h", label = "Wind", sky = sky)
            MetricItem(icon = Icons.Default.Thermostat, value = "${current.feelsLikeC.toInt()}°C", label = "Feels like", sky = sky)
            if (current.uv > 0) {
                MetricItem(
                    icon = Icons.Default.WbSunny,
                    value = "UV ${current.uv.toInt()}",
                    label = "Índice UV",
                    sky = sky,
                    tint = uvColor(current.uv),
                )
            }
        }
    }
}

@Composable
private fun MetricItem(
    icon: ImageVector,
    value: String,
    label: String,
    sky: SkyColors,
    tint: Color = sky.textMuted,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xMedium),
        modifier = modifier,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(WeatherTheme.IconSize.medium))
        Column(verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xSmall)) {
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WeatherTheme.Colors.onSky)
            Text(text = label, fontSize = 10.sp, color = sky.textMuted)
        }
    }
}
