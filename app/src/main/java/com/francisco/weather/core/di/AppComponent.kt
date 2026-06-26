package com.francisco.weather.core.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.francisco.weather.feature.dashboard.di.DashboardModule
import com.francisco.weather.feature.forecast.di.ForecastModule
import com.francisco.weather.feature.search.di.SearchModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        DatabaseModule::class,
        SearchModule::class,
        ForecastModule::class,
        DashboardModule::class,
        ViewModelModule::class,
    ],
)
interface AppComponent {

    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
