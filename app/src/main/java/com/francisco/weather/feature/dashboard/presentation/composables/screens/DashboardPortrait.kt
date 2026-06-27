package com.francisco.weather.feature.dashboard.presentation.composables.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.francisco.weather.core.domain.recent.RecentSearch
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.dashboard.presentation.DashboardState
import com.francisco.weather.feature.dashboard.presentation.composables.MetricsCard
import com.francisco.weather.feature.dashboard.presentation.composables.MyLocationCard
import com.francisco.weather.feature.dashboard.presentation.composables.RecentSearchesSection
import com.francisco.weather.feature.dashboard.presentation.composables.SearchPill
import com.francisco.weather.feature.dashboard.presentation.composables.WorldCupStadiumsSection

@Composable
internal fun DashboardPortrait(
    state: DashboardState,
    sky: SkyColors,
    context: Context,
    onOpenSearch: () -> Unit,
    onUseLocation: () -> Unit,
    onOpenForecast: () -> Unit,
    onOpenRecentForecast: (RecentSearch) -> Unit,
    onClearRecents: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(WeatherTheme.Size.none),
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xLarge),
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(start = WeatherTheme.Size.xLarge, end = WeatherTheme.Size.xLarge, top = WeatherTheme.Size.xLarge, bottom = WeatherTheme.Size.medium)
            .verticalScroll(rememberScrollState()),
    ) {
        SearchPill(sky = sky, onClick = onOpenSearch, modifier = Modifier.fillMaxWidth())

        MyLocationCard(
            state = state,
            sky = sky,
            context = context,
            onUseLocation = onUseLocation,
            onOpenForecast = onOpenForecast,
            showMetricsInline = false,
            modifier = Modifier.fillMaxWidth(),
        )

        val currentWeather = state.currentWeather
        if (currentWeather != null) {
            MetricsCard(
                forecast = currentWeather,
                sky = sky,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (state.recentSearches.isNotEmpty()) {
            RecentSearchesSection(
                recents = state.recentSearches,
                sky = sky,
                onClear = onClearRecents,
                onSelect = onOpenRecentForecast,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (state.stadiums.isNotEmpty()) {
            WorldCupStadiumsSection(
                stadiums = state.stadiums,
                sky = sky,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(WeatherTheme.Size.medium))
    }
}
