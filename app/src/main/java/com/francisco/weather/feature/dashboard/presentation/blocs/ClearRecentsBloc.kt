package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.core.domain.recent.RecentSearchRepository
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState

class ClearRecentsBloc(
    private val recentRepository: RecentSearchRepository,
) : BaseBloc<DashboardEvent.ClearRecents, DashboardState>() {

    override val tag = "ClearRecentsBloc"

    override suspend fun handleEvent(
        event: DashboardEvent.ClearRecents,
        updateState: suspend ((DashboardState) -> DashboardState) -> Unit,
    ) {
        recentRepository.clear()
    }
}
