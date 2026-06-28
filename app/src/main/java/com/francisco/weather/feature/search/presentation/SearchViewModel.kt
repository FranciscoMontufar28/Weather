package com.francisco.weather.feature.search.presentation

import androidx.lifecycle.viewModelScope
import com.francisco.weather.core.bloc.BlocViewModel
import com.francisco.weather.feature.search.domain.usecase.SearchLocationsUseCase
import com.francisco.weather.feature.search.domain.usecase.SearchResults
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
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
                            errorRes  = null,
                        )
                        SearchResults.Loading -> state.copy(
                            isLoading = true,
                            errorRes  = null,
                        )
                        is SearchResults.Success -> state.copy(
                            locations = result.locations,
                            isLoading = false,
                            errorRes  = null,
                        )
                        is SearchResults.Error -> state.copy(
                            locations = emptyList(),
                            isLoading = false,
                            errorRes  = result.messageRes,
                        )
                    }
                }
            }
        }
    }
}
