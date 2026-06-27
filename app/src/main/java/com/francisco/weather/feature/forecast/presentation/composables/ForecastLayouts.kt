package com.francisco.weather.feature.forecast.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.forecast.domain.model.ForecastData

@Composable
internal fun ForecastPortrait(
    forecast: ForecastData,
    sky: SkyColors,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(WeatherTheme.Size.none),
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = WeatherTheme.Size.xLarge),
    ) {
        item { Spacer(Modifier.height(WeatherTheme.Size.xSmall)) }

        if (forecast.alerts.isNotEmpty()) {
            item {
                AlertsBanner(alerts = forecast.alerts, sky = sky)
            }
        }

        item {
            LocationHeader(forecast = forecast, sky = sky)
        }

        items(forecast.days, key = { it.date }) { day ->
            DayGlassCard(day = day, sky = sky)
        }

        item { Spacer(Modifier.height(WeatherTheme.Size.huge)) }
    }
}

@Composable
internal fun ForecastLandscape(
    forecast: ForecastData,
    sky: SkyColors,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.large),
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = WeatherTheme.Size.huge, vertical = WeatherTheme.Size.xSmall)
            .padding(bottom = WeatherTheme.Size.xLarge),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
            modifier = Modifier
                .width(312.dp)
                .fillMaxHeight(),
        ) {
            LandscapeAppBar(onBack = onBack)
            CurrentWeatherCard(forecast = forecast, sky = sky, modifier = Modifier.weight(1f))
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            if (forecast.alerts.isNotEmpty()) {
                AlertsBanner(alerts = forecast.alerts, sky = sky)
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = WeatherTheme.Size.xSmall, vertical = WeatherTheme.Size.xSmall),
            ) {
                Text(
                    text = "3-Day Forecast",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = WeatherTheme.Colors.onSky,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                forecast.days.forEach { day ->
                    CompactDayCard(day = day, sky = sky, modifier = Modifier.weight(1f).fillMaxHeight())
                }
            }

            MetricsBar(current = forecast.current, sky = sky)
        }
    }
}

@Composable
private fun LandscapeAppBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
        modifier = modifier.fillMaxWidth().height(36.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(34.dp)
                .background(WeatherTheme.Colors.glassFill, CircleShape),
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(34.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = WeatherTheme.Colors.onSky,
                    modifier = Modifier.size(WeatherTheme.IconSize.medium),
                )
            }
        }
        Text(
            text = "Forecast",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = WeatherTheme.Colors.onSky,
        )
    }
}
