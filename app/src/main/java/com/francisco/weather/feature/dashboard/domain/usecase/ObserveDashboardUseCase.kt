package com.francisco.weather.feature.dashboard.domain.usecase

import com.francisco.weather.core.domain.recent.RecentSearchRepository
import com.francisco.weather.feature.dashboard.domain.CachedWeatherRepository
import com.francisco.weather.feature.dashboard.domain.StadiumRepository
import com.francisco.weather.feature.dashboard.domain.model.DashboardData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Combines the three Room-backed flows (recents, stadiums, cached weather) into a single
 * reactive stream for the Dashboard screen.
 *
 * All three underlying DAO queries emit immediately upon collection (empty list / null),
 * so the combined flow always produces its first value on the first coroutine tick — no
 * blocking delay.
 */
@Singleton
class ObserveDashboardUseCase @Inject constructor(
    private val recentSearchRepository: RecentSearchRepository,
    private val stadiumRepository: StadiumRepository,
    private val cachedWeatherRepository: CachedWeatherRepository,
) {
    operator fun invoke(): Flow<DashboardData> =
        combine(
            recentSearchRepository.observeRecent(),
            stadiumRepository.observeStadiums(),
            cachedWeatherRepository.observeCached(),
        ) { recents, stadiums, cached ->
            DashboardData(recentSearches = recents, stadiums = stadiums, cachedWeather = cached)
        }
}
