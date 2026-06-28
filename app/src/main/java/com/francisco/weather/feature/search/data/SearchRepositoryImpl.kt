package com.francisco.weather.feature.search.data

import com.francisco.weather.core.network.WeatherApi
import com.francisco.weather.core.network.WeatherError
import com.francisco.weather.feature.search.data.mapper.toDomain
import com.francisco.weather.feature.search.domain.SearchRepository
import com.francisco.weather.feature.search.domain.model.Location
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
) : SearchRepository {

    override suspend fun search(query: String): Result<List<Location>> = runCatching {
        api.search(query).map { it.toDomain() }
    }.recoverWith()

    private fun <T> Result<T>.recoverWith(): Result<T> = fold(
        onSuccess = { value ->
            if (value is List<*> && value.isEmpty()) {
                Result.failure(WeatherError.Empty)
            } else {
                Result.success(value)
            }
        },
        onFailure = { cause ->
            Timber.e(cause, "Search API call failed")
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
