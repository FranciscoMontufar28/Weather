package com.francisco.weather.feature.forecast.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.francisco.weather.core.i18n.LocalLocaleController
import com.francisco.weather.core.ui.components.SkyScaffold
import com.francisco.weather.core.ui.components.SkyTopBar
import com.francisco.weather.core.ui.sky.rememberSkyColors
import com.francisco.weather.feature.forecast.presentation.composables.ErrorState
import com.francisco.weather.feature.forecast.presentation.composables.LoadingState
import com.francisco.weather.feature.forecast.presentation.composables.screens.ForecastLandscape
import com.francisco.weather.feature.forecast.presentation.composables.screens.ForecastPortrait

@Composable
fun ForecastScreen(
    locationQuery: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ForecastViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sky = rememberSkyColors()

    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Re-fetch whenever the location or the active language changes so condition.text
    // is returned in the newly selected language (via ApiKeyInterceptor's lang= param).
    val language = LocalLocaleController.current.language
    LaunchedEffect(locationQuery, language) {
        viewModel.onEvent(ForecastEvent.Load(locationQuery))
    }

    val forecast  = state.forecast
    val errorRes  = state.errorRes

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

            errorRes != null && forecast == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    ErrorState(
                        message = stringResource(errorRes),
                        onRetry  = { viewModel.onEvent(ForecastEvent.Load(locationQuery)) },
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
