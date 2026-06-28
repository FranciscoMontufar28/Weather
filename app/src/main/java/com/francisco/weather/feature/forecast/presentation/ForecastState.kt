package com.francisco.weather.feature.forecast.presentation

import androidx.annotation.StringRes
import com.francisco.weather.core.bloc.BaseState
import com.francisco.weather.feature.forecast.domain.model.ForecastData

data class ForecastState(
    val forecast: ForecastData? = null,
    val isLoading: Boolean = false,
    /** String resource id for the current error, or null if no error. Resolved via stringResource() in the UI. */
    @StringRes val errorRes: Int? = null,
    /** The location query used to load (needed for retry). */
    val locationQuery: String = "",
) : BaseState
