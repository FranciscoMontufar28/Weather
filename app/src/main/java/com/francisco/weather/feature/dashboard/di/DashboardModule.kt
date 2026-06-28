package com.francisco.weather.feature.dashboard.di

import com.francisco.weather.feature.dashboard.data.DeviceLocationProvider
import com.francisco.weather.feature.dashboard.data.StadiumRemoteDataSource
import com.francisco.weather.feature.dashboard.domain.LocationProvider
import com.francisco.weather.feature.dashboard.domain.usecase.ClearRecentSearchesUseCase
import com.francisco.weather.feature.dashboard.domain.usecase.LoadCurrentWeatherUseCase
import com.francisco.weather.feature.dashboard.domain.usecase.SyncStadiumsUseCase
import com.francisco.weather.feature.dashboard.presentation.DashboardBlocFactory
import com.francisco.weather.feature.dashboard.presentation.blocs.ClearRecentsBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.GetRemoteStadiumsBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.LoadCurrentWeatherBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.LocationPermissionResultBloc
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DashboardModule {

    @Provides
    @Singleton
    fun provideStadiumRemoteDataSource(): StadiumRemoteDataSource = StadiumRemoteDataSource()

    @Provides
    @Singleton
    fun provideLocationProvider(impl: DeviceLocationProvider): LocationProvider = impl

    @Provides
    @Singleton
    fun provideLoadCurrentWeatherBloc(
        loadCurrentWeather: LoadCurrentWeatherUseCase,
    ): LoadCurrentWeatherBloc = LoadCurrentWeatherBloc(loadCurrentWeather)

    @Provides
    @Singleton
    fun provideClearRecentsBloc(
        clearRecents: ClearRecentSearchesUseCase,
    ): ClearRecentsBloc = ClearRecentsBloc(clearRecents)

    @Provides
    @Singleton
    fun provideGetRemoteStadiumsBloc(
        syncStadiums: SyncStadiumsUseCase,
    ): GetRemoteStadiumsBloc = GetRemoteStadiumsBloc(syncStadiums)

    @Provides
    @Singleton
    fun provideLocationPermissionResultBloc(): LocationPermissionResultBloc =
        LocationPermissionResultBloc()

    @Provides
    @Singleton
    fun provideDashboardBlocFactory(
        loadCurrentWeatherBloc: LoadCurrentWeatherBloc,
        clearRecentsBloc: ClearRecentsBloc,
        getRemoteStadiumsBloc: GetRemoteStadiumsBloc,
        locationPermissionResultBloc: LocationPermissionResultBloc,
    ): DashboardBlocFactory = DashboardBlocFactory(
        loadCurrentWeatherBloc = loadCurrentWeatherBloc,
        clearRecentsBloc = clearRecentsBloc,
        getRemoteStadiumsBloc = getRemoteStadiumsBloc,
        locationPermissionResultBloc = locationPermissionResultBloc,
    )
}
