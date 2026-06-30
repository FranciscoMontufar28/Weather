package com.francisco.weather.feature.forecast.presentation.blocs

import com.francisco.weather.R
import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.core.network.toErrorRes
import timber.log.Timber
import com.francisco.weather.feature.forecast.domain.usecase.GetForecastUseCase
import com.francisco.weather.feature.forecast.presentation.ForecastEvent
import com.francisco.weather.feature.forecast.presentation.ForecastState

class LoadForecastBloc(
    private val getForecast: GetForecastUseCase,
) : BaseBloc<ForecastEvent.Load, ForecastState>() {

    override suspend fun handleEvent(
        event: ForecastEvent.Load,
        updateState: suspend ((ForecastState) -> ForecastState) -> Unit,
    ) {
        updateState { it.copy(isLoading = true, errorRes = null, locationQuery = event.locationQuery) }

        getForecast(event.locationQuery).fold(
            onSuccess = { forecast ->
                updateState { it.copy(forecast = forecast, isLoading = false, errorRes = null) }
            },
            onFailure = { error ->
                Timber.e(error, "Failed to load forecast")
                updateState {
                    it.copy(
                        isLoading = false,
                        errorRes  = error.toErrorRes(R.string.error_forecast_load),
                    )
                }
            },
        )
    }
}
