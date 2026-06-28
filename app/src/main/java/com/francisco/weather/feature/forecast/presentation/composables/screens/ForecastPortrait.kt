package com.francisco.weather.feature.forecast.presentation.composables.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import com.francisco.weather.feature.forecast.presentation.composables.AlertsBanner
import com.francisco.weather.feature.forecast.presentation.composables.DayGlassCard
import com.francisco.weather.feature.forecast.presentation.composables.LocationHeader

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
