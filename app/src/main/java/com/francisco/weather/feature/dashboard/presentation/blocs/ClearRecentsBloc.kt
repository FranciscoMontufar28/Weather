package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.feature.dashboard.domain.usecase.ClearRecentSearchesUseCase
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState

class ClearRecentsBloc(
    private val clearRecents: ClearRecentSearchesUseCase,
) : BaseBloc<DashboardEvent.ClearRecents, DashboardState>() {

    override suspend fun handleEvent(
        event: DashboardEvent.ClearRecents,
        updateState: suspend ((DashboardState) -> DashboardState) -> Unit,
    ) {
        clearRecents()
    }
}
