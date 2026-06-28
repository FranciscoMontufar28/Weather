package com.francisco.weather.feature.forecast.di

import com.francisco.weather.feature.forecast.data.ForecastRepositoryImpl
import com.francisco.weather.feature.forecast.domain.ForecastRepository
import com.francisco.weather.feature.forecast.domain.usecase.GetForecastUseCase
import com.francisco.weather.feature.forecast.presentation.ForecastBlocFactory
import com.francisco.weather.feature.forecast.presentation.blocs.ClearForecastErrorBloc
import com.francisco.weather.feature.forecast.presentation.blocs.LoadForecastBloc
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class ForecastModule {

    @Binds
    @Singleton
    abstract fun bindForecastRepository(impl: ForecastRepositoryImpl): ForecastRepository

    companion object {

        @Provides
        @Singleton
        fun provideLoadForecastBloc(getForecast: GetForecastUseCase): LoadForecastBloc =
            LoadForecastBloc(getForecast)

        @Provides
        @Singleton
        fun provideForecastBlocFactory(
            loadForecastBloc: LoadForecastBloc,
            clearErrorBloc: ClearForecastErrorBloc,
        ): ForecastBlocFactory =
            ForecastBlocFactory(loadForecastBloc, clearErrorBloc)
    }
}
