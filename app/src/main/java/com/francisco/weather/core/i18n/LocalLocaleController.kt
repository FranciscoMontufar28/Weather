package com.francisco.weather.core.i18n

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Provides the [LocaleManager] to the composition tree without prop-drilling.
 * Mirrors the existing [com.francisco.weather.core.di.LocalViewModelFactory] pattern.
 *
 * Wire in [com.francisco.weather.MainActivity]:
 *   CompositionLocalProvider(LocalLocaleController provides localeManager) { ... }
 */
val LocalLocaleController = staticCompositionLocalOf<LocaleManager> {
    error(
        "No LocaleManager provided. " +
            "Wrap your content with CompositionLocalProvider(LocalLocaleController provides …)."
    )
}
