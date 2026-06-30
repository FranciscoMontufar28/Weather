package com.francisco.weather.feature.search.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.feature.search.domain.usecase.SearchLocationsUseCase
import com.francisco.weather.feature.search.presentation.SearchEvent
import com.francisco.weather.feature.search.presentation.SearchState

/**
 * Handles [SearchEvent.QueryChanged].
 *
 * Updates the query in state immediately and forwards the raw query to
 * [SearchLocationsUseCase]. The use case owns the reactive pipeline
 * (debounce, distinctUntilChanged, flatMapLatest) — no delay or network call here.
 * Results are collected in SearchViewModel.init and mapped back to state there.
 */
class SearchQueryBloc(
    private val searchLocations: SearchLocationsUseCase,
) : BaseBloc<SearchEvent.QueryChanged, SearchState>() {

    override suspend fun handleEvent(
        event: SearchEvent.QueryChanged,
        updateState: suspend ((SearchState) -> SearchState) -> Unit,
    ) {
        updateState { it.copy(query = event.query, errorRes = null) }
        searchLocations.setQuery(event.query)
    }
}
