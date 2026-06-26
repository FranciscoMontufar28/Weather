package com.francisco.weather

import android.app.Application
import com.francisco.weather.core.di.AppComponent
import com.francisco.weather.core.di.DaggerAppComponent

class WeatherApplication : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
    }
}
