package com.francisco.weather.feature.search.presentation

import com.francisco.weather.core.bloc.BaseState
import com.francisco.weather.feature.search.domain.model.Location

data class SearchState(
    val query: String = "",
    val locations: List<Location> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
) : BaseState
