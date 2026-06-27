package com.francisco.weather.feature.forecast.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.forecast.domain.model.HourWeather

@Composable
internal fun HourlyStrip(
    hours: List<HourWeather>,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(WeatherTheme.Size.xMedium),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(hours, key = { it.time }) { hour ->
            HourChip(hour = hour, sky = sky)
        }
    }
}

@Composable
private fun HourChip(
    hour: HourWeather,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    val timeLabel = hour.time.takeLast(5)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(WeatherTheme.Colors.glassFill, RoundedCornerShape(WeatherTheme.Radius.small))
            .padding(horizontal = WeatherTheme.Size.xMedium, vertical = WeatherTheme.Size.small),
    ) {
        Text(text = timeLabel, fontSize = 10.sp, color = sky.textMuted)
        Spacer(Modifier.height(WeatherTheme.Size.xSmall))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(hour.condition.iconUrl).crossfade(true).build(),
            contentDescription = hour.condition.text,
            modifier = Modifier.size(WeatherTheme.IconSize.large),
        )
        Spacer(Modifier.height(WeatherTheme.Size.xSmall))
        Text(
            text = "${hour.tempC.toInt()}°",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = WeatherTheme.Colors.onSky,
        )
        if (hour.chanceOfRain > 0) {
            Text(text = "${hour.chanceOfRain}%", fontSize = 10.sp, color = Color(0xFF90CAF9))
        }
    }
}
