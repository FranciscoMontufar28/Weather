package com.francisco.weather.feature.forecast.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.feature.forecast.presentation.ForecastEvent
import com.francisco.weather.feature.forecast.presentation.ForecastState
import javax.inject.Inject

class ClearForecastErrorBloc @Inject constructor() : BaseBloc<ForecastEvent.ClearError, ForecastState>() {

    override suspend fun handleEvent(
        event: ForecastEvent.ClearError,
        updateState: suspend ((ForecastState) -> ForecastState) -> Unit,
    ) {
        updateState { it.copy(errorRes = null) }
    }
}
