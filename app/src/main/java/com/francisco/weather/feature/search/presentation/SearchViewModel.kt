package com.francisco.weather.feature.search.presentation

import androidx.lifecycle.viewModelScope
import com.francisco.weather.core.bloc.BlocViewModel
import com.francisco.weather.feature.search.domain.usecase.SearchLocationsUseCase
import com.francisco.weather.feature.search.domain.usecase.SearchResults
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    override val factory: SearchBlocFactory,
    private val searchLocations: SearchLocationsUseCase,
) : BlocViewModel<SearchEvent, SearchState>(SearchState()) {

    init {
        viewModelScope.launch {
            searchLocations().collect { result ->
                safeUpdateState { state ->
                    when (result) {
                        SearchResults.Empty -> state.copy(
                            locations = emptyList(),
                            isLoading = false,
                            error = null,
                        )
                        SearchResults.Loading -> state.copy(
                            isLoading = true,
                            error = null,
                        )
                        is SearchResults.Success -> state.copy(
                            locations = result.locations,
                            isLoading = false,
                            error = null,
                        )
                        is SearchResults.Error -> state.copy(
                            locations = emptyList(),
                            isLoading = false,
                            error = result.message,
                        )
                    }
                }
            }
        }
    }
}
