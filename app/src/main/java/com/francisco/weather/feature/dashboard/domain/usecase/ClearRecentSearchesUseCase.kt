package com.francisco.weather.feature.dashboard.domain.usecase

import com.francisco.weather.core.domain.recent.RecentSearchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClearRecentSearchesUseCase @Inject constructor(
    private val repository: RecentSearchRepository,
) {
    suspend operator fun invoke() = repository.clear()
}
