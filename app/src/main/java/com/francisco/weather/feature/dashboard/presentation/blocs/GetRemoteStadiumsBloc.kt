package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.feature.dashboard.domain.StadiumRepository
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState

class GetRemoteStadiumsBloc(
    private val repository: StadiumRepository,
) : BaseBloc<DashboardEvent.GetRemoteStadiums, DashboardState>() {

    override val tag = "GetRemoteStadiumsBloc"

    override suspend fun handleEvent(
        event: DashboardEvent.GetRemoteStadiums,
        updateState: suspend ((DashboardState) -> DashboardState) -> Unit,
    ) {
        updateState { it.copy(isLoadingStadiums = true) }
        runCatching { repository.syncFromRemote() }
        updateState { it.copy(isLoadingStadiums = false) }
    }
}
