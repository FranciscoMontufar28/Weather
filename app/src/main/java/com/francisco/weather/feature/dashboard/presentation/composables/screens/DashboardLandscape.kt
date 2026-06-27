package com.francisco.weather.feature.dashboard.presentation.composables.screens

import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.francisco.weather.core.domain.recent.RecentSearch
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.dashboard.presentation.DashboardState
import com.francisco.weather.feature.dashboard.presentation.composables.DashboardTopBar
import com.francisco.weather.feature.dashboard.presentation.composables.MyLocationCard
import com.francisco.weather.feature.dashboard.presentation.composables.RecentHeader
import com.francisco.weather.feature.dashboard.presentation.composables.RecentSearchesGrid
import com.francisco.weather.feature.dashboard.presentation.composables.SearchPill
import com.francisco.weather.feature.dashboard.presentation.composables.WorldCupStadiumsSection

@Composable
internal fun DashboardLandscape(
    state: DashboardState,
    sky: SkyColors,
    context: Context,
    onOpenSearch: () -> Unit,
    onUseLocation: () -> Unit,
    onOpenForecast: () -> Unit,
    onOpenRecentForecast: (RecentSearch) -> Unit,
    onClearRecents: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.large),
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = WeatherTheme.Size.xLarge, vertical = WeatherTheme.Size.medium),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
            modifier = Modifier
                .width(344.dp)
                .fillMaxHeight(),
        ) {
            DashboardTopBar(sky = sky)
            MyLocationCard(
                state = state,
                sky = sky,
                context = context,
                onUseLocation = onUseLocation,
                onOpenForecast = onOpenForecast,
                showMetricsInline = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
        ) {
            SearchPill(sky = sky, onClick = onOpenSearch, modifier = Modifier.fillMaxWidth())

            if (state.recentSearches.isNotEmpty()) {
                RecentHeader(sky = sky, onClear = onClearRecents)
                RecentSearchesGrid(
                    recents = state.recentSearches,
                    sky = sky,
                    onSelect = onOpenRecentForecast,
                )
            }

            if (state.stadiums.isNotEmpty()) {
                WorldCupStadiumsSection(
                    stadiums = state.stadiums,
                    sky = sky,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
