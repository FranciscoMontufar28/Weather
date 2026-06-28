package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.feature.dashboard.domain.usecase.ObserveStadiumsUseCase
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState

class ObserveStadiumsBloc(
    private val observeStadiums: ObserveStadiumsUseCase,
) : BaseBloc<DashboardEvent.ObserveStadiums, DashboardState>() {

    override val tag = "ObserveStadiumsBloc"

    override suspend fun handleEvent(
        event: DashboardEvent.ObserveStadiums,
        updateState: suspend ((DashboardState) -> DashboardState) -> Unit,
    ) {
        observeStadiums().collect { stadiums ->
            updateState { it.copy(stadiums = stadiums) }
        }
    }
}
