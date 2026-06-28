package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.feature.dashboard.domain.usecase.ObserveRecentSearchesUseCase
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState

class ObserveRecentsBloc(
    private val observeRecents: ObserveRecentSearchesUseCase,
) : BaseBloc<DashboardEvent.ObserveRecents, DashboardState>() {

    override val tag = "ObserveRecentsBloc"

    override suspend fun handleEvent(
        event: DashboardEvent.ObserveRecents,
        updateState: suspend ((DashboardState) -> DashboardState) -> Unit,
    ) {
        observeRecents().collect { recents ->
            updateState { it.copy(recentSearches = recents) }
        }
    }
}
