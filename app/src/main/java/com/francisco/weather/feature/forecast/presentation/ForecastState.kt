package com.francisco.weather.feature.forecast.presentation

import com.francisco.weather.core.bloc.BaseState
import com.francisco.weather.feature.forecast.domain.model.ForecastData

data class ForecastState(
    val forecast: ForecastData? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    /** The location query used to load (needed for retry). */
    val locationQuery: String = "",
) : BaseState
