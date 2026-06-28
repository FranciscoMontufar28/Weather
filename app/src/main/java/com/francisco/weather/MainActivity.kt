package com.francisco.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.francisco.weather.core.di.LocalViewModelFactory
import com.francisco.weather.core.i18n.LocalLocaleController
import com.francisco.weather.core.i18n.ProvideAppLocale
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.navigation.WeatherNavHost

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val component       = (application as WeatherApplication).appComponent
        val viewModelFactory = component.viewModelFactory()
        val localeManager   = component.localeManager()

        setContent {
            CompositionLocalProvider(
                LocalViewModelFactory  provides viewModelFactory,
                LocalLocaleController provides localeManager,
            ) {
                // ProvideAppLocale overrides LocalContext + LocalConfiguration so that
                // stringResource() resolves to the user's chosen language in-place,
                // without recreating the Activity or replaying the splash animation.
                ProvideAppLocale(localeManager) {
                    WeatherTheme {
                        WeatherNavHost()
                    }
                }
            }
        }
    }
}
