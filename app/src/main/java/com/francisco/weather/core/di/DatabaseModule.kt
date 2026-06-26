package com.francisco.weather.core.di

import android.content.Context
import androidx.room.Room
import com.francisco.weather.core.data.local.WeatherDatabase
import com.francisco.weather.core.data.local.recent.RecentSearchDao
import com.francisco.weather.core.data.local.stadium.StadiumDao
import com.francisco.weather.core.data.local.weather.CachedWeatherDao
import com.francisco.weather.core.data.recent.RecentSearchRepositoryImpl
import com.francisco.weather.core.domain.recent.RecentSearchRepository
import com.francisco.weather.feature.dashboard.data.CachedWeatherRepositoryImpl
import com.francisco.weather.feature.dashboard.data.StadiumRepositoryImpl
import com.francisco.weather.feature.dashboard.domain.CachedWeatherRepository
import com.francisco.weather.feature.dashboard.domain.StadiumRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindRecentSearchRepository(impl: RecentSearchRepositoryImpl): RecentSearchRepository

    @Binds
    @Singleton
    abstract fun bindStadiumRepository(impl: StadiumRepositoryImpl): StadiumRepository

    @Binds
    @Singleton
    abstract fun bindCachedWeatherRepository(impl: CachedWeatherRepositoryImpl): CachedWeatherRepository

    companion object {

        @Provides
        @Singleton
        fun provideWeatherDatabase(context: Context): WeatherDatabase =
            Room.databaseBuilder(
                context,
                WeatherDatabase::class.java,
                "weather.db",
            )
                .fallbackToDestructiveMigration(true)
                .build()

        @Provides
        @Singleton
        fun provideRecentSearchDao(db: WeatherDatabase): RecentSearchDao = db.recentSearchDao()

        @Provides
        @Singleton
        fun provideStadiumDao(db: WeatherDatabase): StadiumDao = db.stadiumDao()

        @Provides
        @Singleton
        fun provideCachedWeatherDao(db: WeatherDatabase): CachedWeatherDao = db.cachedWeatherDao()
    }
}
