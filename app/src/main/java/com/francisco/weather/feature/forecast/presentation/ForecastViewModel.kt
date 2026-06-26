package com.francisco.weather.feature.forecast.presentation

import com.francisco.weather.core.bloc.BlocViewModel
import javax.inject.Inject

class ForecastViewModel @Inject constructor(
    override val factory: ForecastBlocFactory,
) : BlocViewModel<ForecastEvent, ForecastState>(ForecastState())
