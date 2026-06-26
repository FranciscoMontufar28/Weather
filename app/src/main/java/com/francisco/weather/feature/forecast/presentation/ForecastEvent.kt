package com.francisco.weather.feature.forecast.presentation

import com.francisco.weather.core.bloc.BaseEvent

sealed class ForecastEvent : BaseEvent {

    /** Load forecast for [locationQuery] (triggered on screen entry or retry). */
    data class Load(val locationQuery: String) : ForecastEvent()

    /** User dismissed / cleared an error. */
    data object ClearError : ForecastEvent()
}
