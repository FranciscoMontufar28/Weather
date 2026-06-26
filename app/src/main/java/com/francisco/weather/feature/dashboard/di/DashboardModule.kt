package com.francisco.weather.feature.dashboard.di

import com.francisco.weather.core.domain.recent.RecentSearchRepository
import com.francisco.weather.feature.dashboard.data.StadiumRemoteDataSource
import com.francisco.weather.feature.dashboard.domain.CachedWeatherRepository
import com.francisco.weather.feature.dashboard.domain.StadiumRepository
import com.francisco.weather.feature.dashboard.presentation.DashboardBlocFactory
import com.francisco.weather.feature.dashboard.presentation.blocs.ClearRecentsBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.GetRemoteStadiumsBloc
import com.francisco.weather.feature.dashboard.presentation.blocs.LoadCurrentWeatherBloc
import com.francisco.weather.feature.forecast.domain.ForecastRepository
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
        repository: ForecastRepository,
        cacheRepository: CachedWeatherRepository,
    ): LoadCurrentWeatherBloc = LoadCurrentWeatherBloc(repository, cacheRepository)

    @Provides
    @Singleton
    fun provideClearRecentsBloc(recentRepository: RecentSearchRepository): ClearRecentsBloc =
        ClearRecentsBloc(recentRepository)

    @Provides
    @Singleton
    fun provideGetRemoteStadiumsBloc(stadiumRepository: StadiumRepository): GetRemoteStadiumsBloc =
        GetRemoteStadiumsBloc(stadiumRepository)

    @Provides
    @Singleton
    fun provideDashboardBlocFactory(
        loadCurrentWeatherBloc: LoadCurrentWeatherBloc,
        clearRecentsBloc: ClearRecentsBloc,
        getRemoteStadiumsBloc: GetRemoteStadiumsBloc,
    ): DashboardBlocFactory = DashboardBlocFactory(loadCurrentWeatherBloc, clearRecentsBloc, getRemoteStadiumsBloc)
}
