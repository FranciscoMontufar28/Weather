package com.francisco.weather.core.i18n

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Overrides [LocalContext] and [LocalConfiguration] so that every `stringResource()` call
 * below this composable resolves to the user's chosen language — without restarting the
 * Activity or replaying the splash animation.
 *
 * Implementation notes (Compose UI 1.7.x / BOM 2024.12.01):
 *   • `stringResource()` reads `LocalContext.current.resources`, NOT a separate `LocalResources`.
 *     (On upgrade to Compose 1.8+, also provide `LocalResources` here.)
 *   • We key the localized context on BOTH [LocaleManager.language] AND the live
 *     [LocalConfiguration] so rotation (orientation/screenSize — listed in android:configChanges)
 *     still rebuilds the configuration correctly, preserving ORIENTATION_LANDSCAPE checks.
 *   • [LocalizedContextWrapper] keeps the Activity as its base so that direct
 *     `context.startActivity(...)` calls in [DashboardScreen] don't require
 *     FLAG_ACTIVITY_NEW_TASK (unlike a bare createConfigurationContext result).
 */
@Composable
fun ProvideAppLocale(
    localeManager: LocaleManager,
    content: @Composable () -> Unit,
) {
    val language   = localeManager.language        // reads mutableState → recomposes on change
    val baseCtx    = LocalContext.current           // the Activity
    val baseCfg    = LocalConfiguration.current    // updates on rotation (configChanges)

    val locale = remember(language) { Locale.forLanguageTag(language.tag) }

    val localizedConfig = remember(language, baseCfg) {
        Configuration(baseCfg).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
    }

    val localizedContext = remember(language, baseCfg) {
        LocalizedContextWrapper(
            base = baseCtx,
            localizedResources = baseCtx.createConfigurationContext(localizedConfig).resources,
        )
    }

    CompositionLocalProvider(
        LocalConfiguration provides localizedConfig,
        LocalContext provides localizedContext,
        content = content,
    )
}

private class LocalizedContextWrapper(
    base: Context,
    private val localizedResources: Resources,
) : ContextWrapper(base) {
    override fun getResources(): Resources = localizedResources
    override fun getAssets(): AssetManager = localizedResources.assets
}
