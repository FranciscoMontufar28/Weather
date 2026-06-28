package com.francisco.weather.feature.forecast.presentation.composables.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.francisco.weather.R
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import com.francisco.weather.feature.forecast.presentation.composables.AlertsBanner
import com.francisco.weather.feature.forecast.presentation.composables.CompactDayCard
import com.francisco.weather.feature.forecast.presentation.composables.CurrentWeatherCard
import com.francisco.weather.feature.forecast.presentation.composables.LandscapeAppBar
import com.francisco.weather.feature.forecast.presentation.composables.MetricsBar

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = WeatherTheme.Size.xSmall,
                        vertical = WeatherTheme.Size.xSmall
                    ),
            ) {
                Text(
                    text = stringResource(R.string.forecast_section_3day),
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
                    CompactDayCard(
                        day = day,
                        sky = sky,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }

            MetricsBar(current = forecast.current, sky = sky)
        }
    }
}