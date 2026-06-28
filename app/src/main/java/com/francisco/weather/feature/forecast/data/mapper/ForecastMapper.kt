package com.francisco.weather.feature.forecast.data.mapper

import com.francisco.weather.feature.forecast.data.dto.AlertDto
import com.francisco.weather.feature.forecast.data.dto.AstroDto
import com.francisco.weather.feature.forecast.data.dto.ConditionDto
import com.francisco.weather.feature.forecast.data.dto.ForecastDayDto
import com.francisco.weather.feature.forecast.data.dto.ForecastResponseDto
import com.francisco.weather.feature.forecast.data.dto.HourDto
import com.francisco.weather.feature.forecast.domain.model.Astro
import com.francisco.weather.feature.forecast.domain.model.Condition
import com.francisco.weather.feature.forecast.domain.model.CurrentWeather
import com.francisco.weather.feature.forecast.domain.model.DayWeather
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import com.francisco.weather.feature.forecast.domain.model.HourWeather
import com.francisco.weather.feature.forecast.domain.model.WeatherAlert

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
    id = java.util.UUID.randomUUID().toString(),
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
