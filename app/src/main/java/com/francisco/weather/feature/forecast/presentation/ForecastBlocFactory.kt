package com.francisco.weather.feature.forecast.presentation

import com.francisco.weather.core.bloc.BaseBlocFactory
import com.francisco.weather.core.bloc.blocMapOf
import com.francisco.weather.core.bloc.with
import com.francisco.weather.feature.forecast.presentation.blocs.ClearForecastErrorBloc
import com.francisco.weather.feature.forecast.presentation.blocs.LoadForecastBloc

class ForecastBlocFactory(
    loadForecastBloc: LoadForecastBloc,
    clearErrorBloc: ClearForecastErrorBloc,
) : BaseBlocFactory<ForecastEvent, ForecastState>() {

    override val blocs = blocMapOf(
        ForecastEvent.Load::class with loadForecastBloc,
        ForecastEvent.ClearError::class with clearErrorBloc,
    )
}
