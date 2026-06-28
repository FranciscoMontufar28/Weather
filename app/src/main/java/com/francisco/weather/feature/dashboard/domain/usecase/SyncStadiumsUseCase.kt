package com.francisco.weather.feature.dashboard.domain.usecase

import com.francisco.weather.feature.dashboard.domain.StadiumRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncStadiumsUseCase @Inject constructor(
    private val repository: StadiumRepository,
) {
    suspend operator fun invoke() = repository.syncFromRemote()
}
