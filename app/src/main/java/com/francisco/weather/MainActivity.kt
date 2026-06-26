package com.francisco.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.francisco.weather.core.di.LocalViewModelFactory
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.navigation.WeatherNavHost

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModelFactory = (application as WeatherApplication).appComponent.viewModelFactory()

        setContent {
            CompositionLocalProvider(LocalViewModelFactory provides viewModelFactory) {
                WeatherTheme {
                    WeatherNavHost()
                }
            }
        }
    }
}
