package com.francisco.weather.feature.dashboard.domain.usecase

import com.francisco.weather.feature.dashboard.domain.StadiumRepository
import com.francisco.weather.feature.dashboard.domain.model.WorldCupStadium
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObserveStadiumsUseCase @Inject constructor(
    private val repository: StadiumRepository,
) {
    operator fun invoke(): Flow<List<WorldCupStadium>> = repository.observeStadiums()
}
