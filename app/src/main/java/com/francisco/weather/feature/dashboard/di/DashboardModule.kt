package com.francisco.weather.feature.dashboard.di

import com.francisco.weather.feature.dashboard.data.StadiumRemoteDataSource
import com.francisco.weather.feature.dashboard.domain.usecase.ClearRecentSearchesUseCase
import com.francisco.weather.feature.dashboard.domain.usecase.LoadCurrentWeatherUseCase
import com.francisco.weather.feature.dashboard.domain.usecase.ObserveCachedWeatherUseCase
import com.francisco.weather.feature.dashboard.domain.usecase.ObserveRecentSearchesUseCase
import com.francisco.weather.feature.dashboard.domain.usecase.ObserveStadiumsUseCase
import com.francisco.weather.feature.dashboard.domain.usecase.SyncStadiumsUseCase
import com.francisco.weather.feature.dashboard.presentation.DashboardBlocFactory
import com.francisco.weather.feature.dashboard.presentation.blocs.ClearRecentsBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.GetRemoteStadiumsBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.LoadCurrentWeatherBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.LocationPermissionResultBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.ObserveCachedWeatherBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.ObserveRecentsBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.ObserveStadiumsBloc
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
    fun provideObserveRecentsBloc(
        observeRecents: ObserveRecentSearchesUseCase,
    ): ObserveRecentsBloc = ObserveRecentsBloc(observeRecents)

    @Provides
    @Singleton
    fun provideObserveStadiumsBloc(
        observeStadiums: ObserveStadiumsUseCase,
    ): ObserveStadiumsBloc = ObserveStadiumsBloc(observeStadiums)

    @Provides
    @Singleton
    fun provideObserveCachedWeatherBloc(
        observeCachedWeather: ObserveCachedWeatherUseCase,
    ): ObserveCachedWeatherBloc = ObserveCachedWeatherBloc(observeCachedWeather)

    @Provides
    @Singleton
    fun provideLocationPermissionResultBloc(
        loadCurrentWeather: LoadCurrentWeatherUseCase,
    ): LocationPermissionResultBloc = LocationPermissionResultBloc(loadCurrentWeather)

    @Provides
    @Singleton
    fun provideDashboardBlocFactory(
        loadCurrentWeatherBloc: LoadCurrentWeatherBloc,
        clearRecentsBloc: ClearRecentsBloc,
        getRemoteStadiumsBloc: GetRemoteStadiumsBloc,
        observeRecentsBloc: ObserveRecentsBloc,
        observeStadiumsBloc: ObserveStadiumsBloc,
        observeCachedWeatherBloc: ObserveCachedWeatherBloc,
        locationPermissionResultBloc: LocationPermissionResultBloc,
    ): DashboardBlocFactory = DashboardBlocFactory(
        loadCurrentWeatherBloc = loadCurrentWeatherBloc,
        clearRecentsBloc = clearRecentsBloc,
        getRemoteStadiumsBloc = getRemoteStadiumsBloc,
        observeRecentsBloc = observeRecentsBloc,
        observeStadiumsBloc = observeStadiumsBloc,
        observeCachedWeatherBloc = observeCachedWeatherBloc,
        locationPermissionResultBloc = locationPermissionResultBloc,
    )
}
