package com.francisco.weather.feature.search.presentation

import androidx.annotation.StringRes
import com.francisco.weather.core.bloc.BaseState
import com.francisco.weather.feature.search.domain.model.Location

data class SearchState(
    val query: String = "",
    val locations: List<Location> = emptyList(),
    val isLoading: Boolean = false,
    /** String resource id for the current search error, or null. Resolved via stringResource() in the UI. */
    @StringRes val errorRes: Int? = null,
) : BaseState
