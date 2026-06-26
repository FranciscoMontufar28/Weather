package com.francisco.weather.feature.search.presentation

import androidx.lifecycle.viewModelScope
import com.francisco.weather.core.bloc.BlocViewModel
import com.francisco.weather.feature.search.presentation.blocs.SearchQueryBloc
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    override val factory: SearchBlocFactory,
    private val searchQueryBloc: SearchQueryBloc,
) : BlocViewModel<SearchEvent, SearchState>(SearchState()) {

    private var searchJob: Job? = null

    override fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    try {
                        searchQueryBloc.run(event, ::safeUpdateState)
                    } catch (e: Exception) {
                        handleError(e)
                    }
                }
            }
            else -> super.onEvent(event)
        }
    }
}
