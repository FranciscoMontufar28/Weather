package com.francisco.weather.feature.forecast.data

import com.francisco.weather.core.network.WeatherApi
import com.francisco.weather.core.network.WeatherError
import com.francisco.weather.feature.forecast.data.mapper.toDomain
import com.francisco.weather.feature.forecast.domain.ForecastRepository
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class ForecastRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
) : ForecastRepository {

    override suspend fun getForecast(locationQuery: String): Result<ForecastData> =
        runCatching {
            api.forecast(locationQuery, days = 3).toDomain()
        }.mapError()

    private fun Result<ForecastData>.mapError(): Result<ForecastData> = fold(
        onSuccess = { Result.success(it) },
        onFailure = { cause ->
            Timber.e(cause, "Forecast API call failed")
            Result.failure(
                when (cause) {
                    is IOException -> WeatherError.Network(cause)
                    is HttpException -> WeatherError.Http(cause.code(), cause.message())
                    else -> WeatherError.Unknown(cause)
                },
            )
        },
    )
}
