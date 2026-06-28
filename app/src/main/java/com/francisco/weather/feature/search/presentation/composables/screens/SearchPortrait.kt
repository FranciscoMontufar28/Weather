package com.francisco.weather.feature.search.presentation.composables.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.search.domain.model.Location
import com.francisco.weather.feature.search.presentation.SearchState
import com.francisco.weather.feature.search.presentation.composables.GlassSearchField
import com.francisco.weather.feature.search.presentation.composables.ResultRow
import com.francisco.weather.feature.search.presentation.composables.ResultsSectionLabel

@Composable
internal fun SearchPortrait(
    state: SearchState,
    sky: SkyColors,
    queryText: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onLocationClick: (Location) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(WeatherTheme.Size.none),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = WeatherTheme.Size.xLarge),
    ) {
        Spacer(Modifier.height(WeatherTheme.Size.huge))

        GlassSearchField(
            query = queryText,
            onQueryChange = onQueryChange,
            onClear = onClear,
            sky = sky,
            focusRequester = focusRequester,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(WeatherTheme.Size.huge))

        Column(modifier = Modifier.fillMaxWidth()) {
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = sky.accent, modifier = Modifier.size(32.dp))
                    }
                }

                state.query.isNotBlank() && state.locations.isNotEmpty() -> {
                    ResultsSectionLabel(sky = sky)
                    Spacer(Modifier.height(WeatherTheme.Size.medium))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        items(state.locations, key = { it.id }) { location ->
                            ResultRow(
                                location = location,
                                sky = sky,
                                onClick = { onLocationClick(location) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }

                state.query.isNotBlank() && !state.isLoading -> {
                    Box(
                        Modifier.fillMaxWidth().padding(top = WeatherTheme.Size.xHuge),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No se encontraron resultados",
                            fontSize = 14.sp,
                            color = sky.textMuted,
                        )
                    }
                }
            }
        }
    }
}