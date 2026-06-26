package com.francisco.weather.feature.dashboard.presentation

import com.francisco.weather.core.bloc.BaseBlocFactory
import com.francisco.weather.core.bloc.blocMapOf
import com.francisco.weather.core.bloc.with
import com.francisco.weather.feature.dashboard.presentation.blocs.ClearRecentsBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.GetRemoteStadiumsBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.LoadCurrentWeatherBloc

class DashboardBlocFactory(
    loadCurrentWeatherBloc: LoadCurrentWeatherBloc,
    clearRecentsBloc: ClearRecentsBloc,
    getRemoteStadiumsBloc: GetRemoteStadiumsBloc,
) : BaseBlocFactory<DashboardEvent, DashboardState>() {

    override val blocs = blocMapOf(
        DashboardEvent.LoadCurrentWeather::class with loadCurrentWeatherBloc,
        DashboardEvent.ClearRecents::class with clearRecentsBloc,
        DashboardEvent.GetRemoteStadiums::class with getRemoteStadiumsBloc,
    )
}
