package com.francisco.weather.feature.search.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.francisco.weather.core.di.LocalViewModelFactory
import com.francisco.weather.core.ui.components.SkyScaffold
import com.francisco.weather.core.ui.sky.rememberSkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.search.domain.model.Location
import com.francisco.weather.feature.search.presentation.composables.SearchHeader
import com.francisco.weather.feature.search.presentation.composables.SearchLandscape
import com.francisco.weather.feature.search.presentation.composables.SearchPortrait

@Composable
fun SearchScreen(
    onLocationSelected: (locationQuery: String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = LocalViewModelFactory.current
    val viewModel: SearchViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sky = rememberSkyColors()
    var queryText by rememberSaveable { mutableStateOf("") }

    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.error) {
        if (state.error != null) viewModel.onEvent(SearchEvent.ClearError)
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    SkyScaffold(
        sky = sky,
        modifier = modifier,
        topBar = {
            if (!isLandscape) {
                SearchHeader(
                    sky = sky,
                    onBack = onNavigateBack,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(start = WeatherTheme.Size.xLarge, end = WeatherTheme.Size.xLarge, top = WeatherTheme.Size.medium),
                )
            }
        },
    ) { innerPadding ->
        val onLocationClick: (Location) -> Unit = { loc ->
            viewModel.onEvent(SearchEvent.LocationSelected(loc))
            onLocationSelected(loc.name)
        }
        if (isLandscape) {
            SearchLandscape(
                state = state,
                sky = sky,
                queryText = queryText,
                onQueryChange = {
                    queryText = it
                    viewModel.onEvent(SearchEvent.QueryChanged(it))
                },
                onClear = {
                    queryText = ""
                    viewModel.onEvent(SearchEvent.QueryChanged(""))
                },
                onLocationClick = onLocationClick,
                onNavigateBack = onNavigateBack,
                focusRequester = focusRequester,
            )
        } else {
            SearchPortrait(
                state = state,
                sky = sky,
                queryText = queryText,
                onQueryChange = {
                    queryText = it
                    viewModel.onEvent(SearchEvent.QueryChanged(it))
                },
                onClear = {
                    queryText = ""
                    viewModel.onEvent(SearchEvent.QueryChanged(""))
                },
                onLocationClick = onLocationClick,
                focusRequester = focusRequester,
                contentPadding = innerPadding,
            )
        }
    }
}

@Preview(name = "Portrait · Day", showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun SearchPortraitPreview() {
    WeatherTheme { Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF123A72), Color(0xFF245CA8), Color(0xFF4E92DA))))) }
}
