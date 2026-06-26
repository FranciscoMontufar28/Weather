package com.francisco.weather.feature.forecast.data.dto

import com.francisco.weather.feature.forecast.domain.model.Condition
import com.francisco.weather.feature.forecast.domain.model.CurrentWeather
import com.francisco.weather.feature.forecast.domain.model.DayWeather
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponseDto(
    val location: LocationInfoDto,
    val current: CurrentDto,
    val forecast: ForecastDto,
)

@Serializable
data class LocationInfoDto(
    val name: String,
    val region: String,
    val country: String,
)

@Serializable
data class CurrentDto(
    @SerialName("temp_c") val tempC: Double,
    @SerialName("humidity") val humidity: Int,
    @SerialName("wind_kph") val windKph: Double,
    @SerialName("feelslike_c") val feelsLikeC: Double,
    val condition: ConditionDto,
    @SerialName("is_day") val isDay: Int = 1,
)

@Serializable
data class ForecastDto(
    val forecastday: List<ForecastDayDto>,
)

@Serializable
data class ForecastDayDto(
    val date: String,
    val day: DayDto,
)

@Serializable
data class DayDto(
    @SerialName("maxtemp_c") val maxTempC: Double,
    @SerialName("mintemp_c") val minTempC: Double,
    @SerialName("avgtemp_c") val avgTempC: Double,
    val condition: ConditionDto,
)

@Serializable
data class ConditionDto(
    val text: String,
    val icon: String,
    val code: Int,
)

// --- Mapping ---

fun ForecastResponseDto.toDomain(): ForecastData = ForecastData(
    locationName = location.name,
    region = location.region,
    country = location.country,
    days = forecast.forecastday.map { it.toDomain() },
    current = CurrentWeather(
        tempC = current.tempC,
        condition = current.condition.toDomain(),
        humidity = current.humidity,
        windKph = current.windKph,
        feelsLikeC = current.feelsLikeC,
    ),
)

fun ForecastDayDto.toDomain(): DayWeather = DayWeather(
    date = date,
    avgTempC = day.avgTempC,
    maxTempC = day.maxTempC,
    minTempC = day.minTempC,
    condition = day.condition.toDomain(),
)

fun ConditionDto.toDomain(): Condition = Condition(
    text = text,
    iconUrl = if (icon.startsWith("//")) "https:$icon" else icon,
    code = code,
)
