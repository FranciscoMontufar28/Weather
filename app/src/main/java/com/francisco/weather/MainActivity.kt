package com.francisco.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.francisco.weather.core.i18n.LocalLocaleController
import com.francisco.weather.core.i18n.LocaleManager
import com.francisco.weather.core.i18n.ProvideAppLocale
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.navigation.WeatherNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var localeManager: LocaleManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(
                LocalLocaleController provides localeManager,
            ) {
                ProvideAppLocale(localeManager) {
                    WeatherTheme {
                        WeatherNavHost()
                    }
                }
            }
        }
    }
}
