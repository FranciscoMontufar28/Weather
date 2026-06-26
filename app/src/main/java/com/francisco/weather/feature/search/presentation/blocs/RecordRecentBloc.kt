package com.francisco.weather.feature.search.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.core.domain.recent.RecentSearchRepository
import com.francisco.weather.feature.search.presentation.SearchEvent
import com.francisco.weather.feature.search.presentation.SearchState

class RecordRecentBloc(
    private val recentRepository: RecentSearchRepository,
) : BaseBloc<SearchEvent.LocationSelected, SearchState>() {

    override val tag = "RecordRecentBloc"

    override suspend fun handleEvent(
        event: SearchEvent.LocationSelected,
        updateState: suspend ((SearchState) -> SearchState) -> Unit,
    ) {
        recentRepository.add(
            name = event.location.name,
            region = event.location.region,
            country = event.location.country,
        )
    }
}
