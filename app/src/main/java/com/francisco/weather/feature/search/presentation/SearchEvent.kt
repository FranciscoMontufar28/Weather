package com.francisco.weather.feature.search.presentation

import com.francisco.weather.core.bloc.BaseEvent
import com.francisco.weather.feature.search.domain.model.Location

sealed class SearchEvent : BaseEvent {
    data class QueryChanged(val query: String) : SearchEvent()
    data object ClearError : SearchEvent()
    data class LocationSelected(val location: Location) : SearchEvent()
}
