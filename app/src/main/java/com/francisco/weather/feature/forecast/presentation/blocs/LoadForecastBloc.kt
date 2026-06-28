package com.francisco.weather.feature.forecast.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.feature.forecast.domain.usecase.GetForecastUseCase
import com.francisco.weather.feature.forecast.presentation.ForecastEvent
import com.francisco.weather.feature.forecast.presentation.ForecastState

class LoadForecastBloc(
    private val getForecast: GetForecastUseCase,
) : BaseBloc<ForecastEvent.Load, ForecastState>() {

    override val tag = "LoadForecastBloc"

    override suspend fun handleEvent(
        event: ForecastEvent.Load,
        updateState: suspend ((ForecastState) -> ForecastState) -> Unit,
    ) {
        updateState { it.copy(isLoading = true, error = null, locationQuery = event.locationQuery) }

        getForecast(event.locationQuery).fold(
            onSuccess = { forecast ->
                updateState { it.copy(forecast = forecast, isLoading = false, error = null) }
            },
            onFailure = { error ->
                updateState {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Error al cargar el pronóstico",
                    )
                }
            },
        )
    }
}
