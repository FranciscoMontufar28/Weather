package com.francisco.weather.feature.dashboard.domain.usecase

import com.francisco.weather.core.domain.recent.RecentSearch
import com.francisco.weather.core.domain.recent.RecentSearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObserveRecentSearchesUseCase @Inject constructor(
    private val repository: RecentSearchRepository,
) {
    operator fun invoke(): Flow<List<RecentSearch>> = repository.observeRecent()
}
