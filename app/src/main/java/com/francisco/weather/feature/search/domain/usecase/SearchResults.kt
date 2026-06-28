package com.francisco.weather.feature.search.domain.usecase

import androidx.annotation.StringRes
import com.francisco.weather.feature.search.domain.model.Location

sealed interface SearchResults {
    data object Empty : SearchResults
    data object Loading : SearchResults
    data class Success(val locations: List<Location>) : SearchResults
    data class Error(@StringRes val messageRes: Int) : SearchResults
}
