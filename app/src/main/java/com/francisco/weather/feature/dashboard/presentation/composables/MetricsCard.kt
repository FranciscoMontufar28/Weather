package com.francisco.weather.feature.dashboard.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import kotlin.math.roundToInt

@Composable
internal fun MetricsRow(
    forecast: ForecastData,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        MetricItem(
            icon = Icons.Default.WaterDrop,
            label = "Humidity",
            value = "${forecast.current.humidity}%",
            sky = sky,
        )
        MetricItem(
            icon = Icons.Default.Air,
            label = "Wind",
            value = "${forecast.current.windKph.roundToInt()} km/h",
            sky = sky,
        )
        MetricItem(
            icon = Icons.Default.Thermostat,
            label = "Feels like",
            value = "${forecast.current.feelsLikeC.roundToInt()}°",
            sky = sky,
        )
    }
}

@Composable
internal fun MetricsCard(
    forecast: ForecastData,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(WeatherTheme.Radius.medium),
        colors = CardDefaults.cardColors(containerColor = WeatherTheme.Colors.glassFill),
        border = BorderStroke(WeatherTheme.Border.thin, WeatherTheme.Colors.glassStroke),
        modifier = modifier,
    ) {
        MetricsRow(
            forecast = forecast,
            sky = sky,
            modifier = Modifier.padding(horizontal = WeatherTheme.Size.medium, vertical = WeatherTheme.Size.large),
        )
    }
}

@Composable
private fun MetricItem(
    icon: ImageVector,
    label: String,
    value: String,
    sky: SkyColors,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xMedium),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = sky.textMuted,
            modifier = Modifier.size(WeatherTheme.IconSize.medium),
        )
        Column(verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xSmall)) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = WeatherTheme.Colors.onSky,
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = sky.textMuted,
            )
        }
    }
}
