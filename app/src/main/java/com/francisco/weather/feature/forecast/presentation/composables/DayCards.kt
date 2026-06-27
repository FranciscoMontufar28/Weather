package com.francisco.weather.feature.forecast.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.forecast.domain.model.DayWeather

@Composable
internal fun DayGlassCard(
    day: DayWeather,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    GlassCard(shape = RoundedCornerShape(WeatherTheme.Radius.medium), modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(WeatherTheme.Size.large)) {
            Text(
                text = day.date.toDayLabel(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = sky.accent,
            )
            Spacer(Modifier.height(WeatherTheme.Size.medium))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(day.condition.iconUrl).crossfade(true).build(),
                    contentDescription = day.condition.text,
                    modifier = Modifier.size(WeatherTheme.IconSize.weather),
                )
                Spacer(Modifier.width(WeatherTheme.Size.medium))
                Text(
                    text = day.condition.text,
                    fontSize = 15.sp,
                    color = WeatherTheme.Colors.onSky,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(WeatherTheme.Size.medium))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${day.avgTempC.toInt()}°",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = WeatherTheme.Colors.onSky,
                    )
                    if (day.uv > 0) {
                        Text(
                            text = "UV ${day.uv.toInt()}",
                            fontSize = 10.sp,
                            color = uvColor(day.uv),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            Spacer(Modifier.height(WeatherTheme.Size.medium))
            HorizontalDivider(color = WeatherTheme.Colors.glassStroke, thickness = WeatherTheme.Border.hairline)
            Spacer(Modifier.height(WeatherTheme.Size.medium))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth(),
            ) {
                TempChip("Mín", day.minTempC, sky)
                TempChip("Prom", day.avgTempC, sky)
                TempChip("Máx", day.maxTempC, sky)
                if (day.chanceOfRain > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = sky.textMuted,
                            modifier = Modifier.size(WeatherTheme.IconSize.small),
                        )
                        Text(
                            text = "${day.chanceOfRain}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WeatherTheme.Colors.onSky,
                        )
                        Text(text = "Lluvia", fontSize = 12.sp, color = sky.textMuted)
                    }
                }
            }

            if (day.hours.isNotEmpty()) {
                Spacer(Modifier.height(WeatherTheme.Size.medium))
                HorizontalDivider(color = WeatherTheme.Colors.glassStroke, thickness = WeatherTheme.Border.hairline)
                Spacer(Modifier.height(WeatherTheme.Size.xMedium))
                HourlyStrip(hours = day.hours, sky = sky)
            }

            if (day.astro.sunrise.isNotBlank()) {
                Spacer(Modifier.height(WeatherTheme.Size.medium))
                HorizontalDivider(color = WeatherTheme.Colors.glassStroke, thickness = WeatherTheme.Border.hairline)
                Spacer(Modifier.height(WeatherTheme.Size.xMedium))
                AstroRow(astro = day.astro, sky = sky)
            }
        }
    }
}

@Composable
internal fun CompactDayCard(
    day: DayWeather,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    GlassCard(shape = RoundedCornerShape(WeatherTheme.Radius.medium), modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = WeatherTheme.Size.xMedium, vertical = WeatherTheme.Size.medium),
        ) {
            Text(
                text = day.date.toShortDayLabel(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = WeatherTheme.Colors.onSky,
            )
            Spacer(Modifier.height(WeatherTheme.Size.xMedium))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(day.condition.iconUrl).crossfade(true).build(),
                contentDescription = day.condition.text,
                modifier = Modifier.size(WeatherTheme.IconSize.weatherSmall),
            )
            Spacer(Modifier.height(WeatherTheme.Size.xMedium))
            Text(
                text = day.condition.text,
                fontSize = 11.sp,
                color = sky.textMuted,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(WeatherTheme.Size.xMedium))
            Text(
                text = "${day.avgTempC.toInt()}°C",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WeatherTheme.Colors.onSky,
            )
            if (day.uv > 0) {
                Spacer(Modifier.height(WeatherTheme.Size.xSmall))
                Text(
                    text = "UV ${day.uv.toInt()}",
                    fontSize = 10.sp,
                    color = uvColor(day.uv),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun TempChip(label: String, temp: Double, sky: SkyColors, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(text = label, fontSize = 12.sp, color = sky.textMuted)
        Text(
            text = "${temp.toInt()}°C",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = WeatherTheme.Colors.onSky,
        )
    }
}
