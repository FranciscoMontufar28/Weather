package com.francisco.weather.feature.dashboard.presentation

import com.francisco.weather.core.bloc.BaseBlocFactory
import com.francisco.weather.core.bloc.blocMapOf
import com.francisco.weather.core.bloc.with
import com.francisco.weather.feature.dashboard.presentation.blocs.ClearRecentsBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.GetRemoteStadiumsBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.GpsStateChangedBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.LoadCurrentWeatherBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.LocationPermissionResultBloc

class DashboardBlocFactory(
    loadCurrentWeatherBloc: LoadCurrentWeatherBloc,
    clearRecentsBloc: ClearRecentsBloc,
    getRemoteStadiumsBloc: GetRemoteStadiumsBloc,
    locationPermissionResultBloc: LocationPermissionResultBloc,
) : BaseBlocFactory<DashboardEvent, DashboardState>() {

    override val blocs = blocMapOf(
        DashboardEvent.LoadCurrentWeather::class with loadCurrentWeatherBloc,
        DashboardEvent.ClearRecents::class with clearRecentsBloc,
        DashboardEvent.GetRemoteStadiums::class with getRemoteStadiumsBloc,
        DashboardEvent.LocationPermissionResult::class with locationPermissionResultBloc,
        DashboardEvent.GpsStateChanged::class with GpsStateChangedBloc(),
    )
}
