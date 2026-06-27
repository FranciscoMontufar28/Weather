package com.francisco.weather.feature.forecast.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import com.francisco.weather.core.di.LocalViewModelFactory
import com.francisco.weather.core.ui.components.SkyScaffold
import com.francisco.weather.core.ui.components.SkyTopBar
import com.francisco.weather.core.ui.sky.rememberSkyColors
import com.francisco.weather.feature.forecast.presentation.composables.ErrorState
import com.francisco.weather.feature.forecast.presentation.composables.ForecastLandscape
import com.francisco.weather.feature.forecast.presentation.composables.ForecastPortrait
import com.francisco.weather.feature.forecast.presentation.composables.LoadingState

@Composable
fun ForecastScreen(
    locationQuery: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = LocalViewModelFactory.current
    val viewModel: ForecastViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sky = rememberSkyColors()

    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(locationQuery) {
        viewModel.onEvent(ForecastEvent.Load(locationQuery))
    }

    val forecast = state.forecast
    val error = state.error

    SkyScaffold(
        sky = sky,
        modifier = modifier,
        topBar = {
            if (!isLandscape && forecast != null) {
                SkyTopBar(sky = sky, title = forecast.locationName, onBack = onNavigateBack)
            }
        },
    ) { innerPadding ->
        when {
            state.isLoading -> LoadingState(sky = sky)

            error != null && forecast == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    ErrorState(
                        message = error,
                        onRetry = { viewModel.onEvent(ForecastEvent.Load(locationQuery)) },
                        sky = sky,
                    )
                }
            }

            forecast != null -> {
                if (isLandscape) {
                    ForecastLandscape(
                        forecast = forecast,
                        sky = sky,
                        onBack = onNavigateBack,
                    )
                } else {
                    ForecastPortrait(
                        forecast = forecast,
                        sky = sky,
                        contentPadding = innerPadding,
                    )
                }
            }
        }
    }
}
