package com.francisco.weather.core.i18n

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for the user's chosen in-app language.
 *
 * • Persists the choice in SharedPreferences (synchronous read at cold start = no flash).
 * • Exposes [language] backed by a private Compose [mutableStateOf] so [ProvideAppLocale]
 *   recomposes the entire tree when [setLanguage] is called, updating all stringResource()
 *   calls in-place.
 * • Calls [Locale.setDefault] on every change so date formatters and the API interceptor
 *   (which reads [Locale.getDefault().language]) pick up the new locale immediately.
 *
 * NOTE: [language] is a read-only computed property backed by a private `_language` state.
 * This separates the mutable write path ([setLanguage]) from the Kotlin property setter so
 * there is no JVM signature clash between the generated `setLanguage` setter and the public
 * method of the same name.
 *
 * Injected as a @Singleton: the same instance is wired into [dagger.hilt.components.SingletonComponent]
 * and exposed via [LocalLocaleController] to composables without prop-drilling.
 */
@Singleton
class LocaleManager @Inject constructor(@ApplicationContext context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun resolveInitial(): AppLanguage =
        if (prefs.contains(KEY_LANG)) {
            // User previously chose a language — honour it.
            AppLanguage.fromTag(prefs.getString(KEY_LANG, null))
        } else {
            // First launch: mirror the device locale (Spanish → SPANISH, else ENGLISH).
            AppLanguage.fromDevice()
        }

    // Private mutable state — write access only via setLanguage().
    private var _language: AppLanguage by mutableStateOf(resolveInitial())

    /** The currently active language. Reading this inside a @Composable subscribes to updates. */
    val language: AppLanguage get() = _language

    init {
        // Synchronously align the JVM default locale so date formatters and the
        // WeatherAPI interceptor see the correct locale before the first frame.
        Locale.setDefault(Locale.forLanguageTag(_language.tag))
    }

    fun setLanguage(target: AppLanguage) {
        if (target == _language) return
        prefs.edit().putString(KEY_LANG, target.tag).apply()
        Locale.setDefault(Locale.forLanguageTag(target.tag))
        _language = target   // mutableState write → triggers ProvideAppLocale recomposition
    }

    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val KEY_LANG   = "app_language"
    }
}
