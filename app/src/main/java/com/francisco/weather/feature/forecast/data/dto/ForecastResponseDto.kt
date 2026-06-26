package com.francisco.weather.feature.forecast.data.dto

import com.francisco.weather.feature.forecast.domain.model.Astro
import com.francisco.weather.feature.forecast.domain.model.Condition
import com.francisco.weather.feature.forecast.domain.model.CurrentWeather
import com.francisco.weather.feature.forecast.domain.model.DayWeather
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import com.francisco.weather.feature.forecast.domain.model.HourWeather
import com.francisco.weather.feature.forecast.domain.model.WeatherAlert
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponseDto(
    val location: LocationInfoDto,
    val current: CurrentDto,
    val forecast: ForecastDto,
    val alerts: AlertsWrapperDto? = null,
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
    @SerialName("uv") val uv: Double = 0.0,
)

@Serializable
data class ForecastDto(
    val forecastday: List<ForecastDayDto>,
)

@Serializable
data class ForecastDayDto(
    val date: String,
    val day: DayDto,
    val astro: AstroDto = AstroDto(),
    val hour: List<HourDto> = emptyList(),
)

@Serializable
data class DayDto(
    @SerialName("maxtemp_c") val maxTempC: Double,
    @SerialName("mintemp_c") val minTempC: Double,
    @SerialName("avgtemp_c") val avgTempC: Double,
    val condition: ConditionDto,
    @SerialName("uv") val uv: Double = 0.0,
    @SerialName("maxwind_kph") val maxWindKph: Double = 0.0,
    @SerialName("daily_chance_of_rain") val dailyChanceOfRain: Int = 0,
    @SerialName("totalprecip_mm") val totalPrecipMm: Double = 0.0,
)

@Serializable
data class AstroDto(
    val sunrise: String = "",
    val sunset: String = "",
    @SerialName("moon_phase") val moonPhase: String = "",
    @SerialName("moon_illumination") val moonIllumination: Int = 0,
)

@Serializable
data class HourDto(
    val time: String = "",
    @SerialName("temp_c") val tempC: Double = 0.0,
    val condition: ConditionDto = ConditionDto("", "", 0),
    @SerialName("chance_of_rain") val chanceOfRain: Int = 0,
    @SerialName("is_day") val isDay: Int = 1,
)

@Serializable
data class ConditionDto(
    val text: String,
    val icon: String,
    val code: Int,
)

// --- Alert DTOs ---

@Serializable
data class AlertsWrapperDto(
    val alert: List<AlertDto> = emptyList(),
)

@Serializable
data class AlertDto(
    val headline: String = "",
    val event: String = "",
    val severity: String = "",
    val areas: String = "",
    val desc: String = "",
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
        uv = current.uv,
    ),
    alerts = alerts?.alert?.map { it.toDomain() } ?: emptyList(),
)

fun ForecastDayDto.toDomain(): DayWeather = DayWeather(
    date = date,
    avgTempC = day.avgTempC,
    maxTempC = day.maxTempC,
    minTempC = day.minTempC,
    condition = day.condition.toDomain(),
    uv = day.uv,
    maxWindKph = day.maxWindKph,
    chanceOfRain = day.dailyChanceOfRain,
    totalPrecipMm = day.totalPrecipMm,
    astro = astro.toDomain(),
    hours = hour.map { it.toDomain() },
)

fun AstroDto.toDomain(): Astro = Astro(
    sunrise = sunrise,
    sunset = sunset,
    moonPhase = moonPhase,
    moonIllumination = moonIllumination,
)

fun HourDto.toDomain(): HourWeather = HourWeather(
    time = time,
    tempC = tempC,
    condition = condition.toDomain(),
    chanceOfRain = chanceOfRain,
    isDay = isDay == 1,
)

fun AlertDto.toDomain(): WeatherAlert = WeatherAlert(
    headline = headline,
    event = event,
    severity = severity,
    areas = areas,
    desc = desc,
)

fun ConditionDto.toDomain(): Condition = Condition(
    text = text,
    iconUrl = if (icon.startsWith("//")) "https:$icon" else icon,
    code = code,
)
