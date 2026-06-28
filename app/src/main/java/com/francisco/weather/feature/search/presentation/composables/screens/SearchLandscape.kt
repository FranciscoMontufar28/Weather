package com.francisco.weather.feature.search.presentation.composables.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.search.domain.model.Location
import com.francisco.weather.feature.search.presentation.SearchState
import com.francisco.weather.feature.search.presentation.composables.GlassSearchField
import com.francisco.weather.feature.search.presentation.composables.ResultsLandscape
import com.francisco.weather.feature.search.presentation.composables.SearchHeader

@Composable
internal fun SearchLandscape(
    state: SearchState,
    sky: SkyColors,
    queryText: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onLocationClick: (Location) -> Unit,
    onNavigateBack: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xHuge),
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = WeatherTheme.Size.huge, vertical = WeatherTheme.Size.large),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.large),
            modifier = Modifier
                .width(312.dp)
                .fillMaxHeight(),
        ) {
            SearchHeader(sky = sky, onBack = onNavigateBack)
            GlassSearchField(
                query = queryText,
                onQueryChange = onQueryChange,
                onClear = onClear,
                sky = sky,
                focusRequester = focusRequester,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            if (state.query.isNotBlank()) {
                ResultsLandscape(
                    state = state,
                    sky = sky,
                    onLocationClick = onLocationClick,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}