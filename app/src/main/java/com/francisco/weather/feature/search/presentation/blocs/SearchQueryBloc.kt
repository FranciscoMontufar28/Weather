package com.francisco.weather.feature.search.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.feature.search.domain.SearchRepository
import com.francisco.weather.feature.search.presentation.SearchEvent
import com.francisco.weather.feature.search.presentation.SearchState
import kotlinx.coroutines.delay

/**
 * Handles [SearchEvent.QueryChanged].
 *
 * Updates the query immediately, debounces 300 ms, then calls the repo.
 * The debounce works because the coroutine launched per event is cancelled by
 * [SearchViewModel.onEvent] before a new one starts.
 */
class SearchQueryBloc(
    private val repository: SearchRepository,
) : BaseBloc<SearchEvent.QueryChanged, SearchState>() {

    override val tag = "SearchQueryBloc"

    override suspend fun handleEvent(
        event: SearchEvent.QueryChanged,
        updateState: suspend ((SearchState) -> SearchState) -> Unit,
    ) {
        val query = event.query.trim()

        // Reflect the query in state immediately
        updateState { it.copy(query = query, error = null) }

        if (query.isBlank()) {
            updateState { it.copy(locations = emptyList(), isLoading = false) }
            return
        }

        // Debounce: wait 300 ms. If a new event arrives, this coroutine is cancelled.
        delay(300L)

        updateState { it.copy(isLoading = true) }

        repository.search(query).fold(
            onSuccess = { locations ->
                updateState {
                    it.copy(
                        locations = locations,
                        isLoading = false,
                        error = null,
                    )
                }
            },
            onFailure = { error ->
                updateState {
                    it.copy(
                        locations = emptyList(),
                        isLoading = false,
                        error = error.message ?: "Error desconocido",
                    )
                }
            },
        )
    }
}
