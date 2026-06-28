package com.francisco.weather.core.network

import androidx.annotation.StringRes
import com.francisco.weather.R

/**
 * Maps any [Throwable] to a localizable string-resource id so error messages
 * remain reactive to runtime language changes (resolved via stringResource in Compose).
 *
 * Pass [fallback] for domain-specific errors not covered by [WeatherError].
 */
@StringRes
fun Throwable.toErrorRes(@StringRes fallback: Int): Int = when (this) {
    is WeatherError.Network -> R.string.error_network
    is WeatherError.Empty   -> R.string.search_no_results
    is WeatherError.Http,
    is WeatherError.Unknown -> R.string.error_unexpected
    else                    -> fallback
}
