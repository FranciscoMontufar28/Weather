package com.francisco.weather.core.network

import com.francisco.weather.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Appends the WeatherAPI key to every outgoing request as a query parameter.
 */
class ApiKeyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("key", BuildConfig.WEATHER_API_KEY)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}
