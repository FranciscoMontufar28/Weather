package com.francisco.weather.core.i18n

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.francisco.weather.R
import java.util.Locale

/**
 * Supported in-app languages.
 * [tag]      : BCP-47 language tag passed to the WeatherAPI lang= param and Locale.forLanguageTag.
 * [labelRes] : Endonym label shown in the flag dropdown (translatable="false" — always its own name).
 * [flagRes]  : Vector drawable representing this language's flag.
 */
enum class AppLanguage(
    val tag: String,
    @StringRes val labelRes: Int,
    @DrawableRes val flagRes: Int,
) {
    ENGLISH("en", R.string.language_english, R.drawable.ic_flag_us),
    SPANISH("es", R.string.language_spanish, R.drawable.ic_flag_co);

    companion object {

        /** Resolve a language from a saved BCP-47 tag; falls back to [fromDevice]. */
        fun fromTag(tag: String?): AppLanguage =
            entries.firstOrNull { it.tag == tag } ?: DEFAULT

        /** Detect the best match from the device locale; falls back to [DEFAULT]. */
        fun fromDevice(): AppLanguage {
            val lang = Locale.getDefault().language
            return entries.firstOrNull { it.tag == lang } ?: DEFAULT
        }

        val DEFAULT = ENGLISH
    }
}
