package com.francisco.weather.feature.forecast.presentation

import com.francisco.weather.core.bloc.BlocViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    override val factory: ForecastBlocFactory,
) : BlocViewModel<ForecastEvent, ForecastState>(ForecastState())
