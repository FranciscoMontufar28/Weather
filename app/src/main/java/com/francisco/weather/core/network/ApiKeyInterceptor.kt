package com.francisco.weather.core.network

import com.francisco.weather.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale

/**
 * Appends common query parameters to every outgoing WeatherAPI request:
 *
 * • `key`  — the API key from BuildConfig.
 * • `lang` — the current JVM default language tag (e.g. "es") so that
 *            `condition.text` is returned in the user's chosen language.
 *            Omitted when the language is English (WeatherAPI default), to
 *            keep URLs shorter and avoid a redundant parameter.
 *
 * Reads [Locale.getDefault()] at request time, so it always reflects the
 * language selected via [com.francisco.weather.core.i18n.LocaleManager]
 * (which calls Locale.setDefault on every language change).
 */
class ApiKeyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl     = originalRequest.url

        val urlBuilder = originalUrl.newBuilder()
            .addQueryParameter("key", BuildConfig.WEATHER_API_KEY)

        // Add lang= only for non-English languages (API default is English).
        val lang = Locale.getDefault().language
        if (lang != "en") {
            urlBuilder.addQueryParameter("lang", lang)
        }

        val newRequest = originalRequest.newBuilder()
            .url(urlBuilder.build())
            .build()

        return chain.proceed(newRequest)
    }
}
