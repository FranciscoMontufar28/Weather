package com.francisco.weather.feature.dashboard.domain

import com.francisco.weather.feature.dashboard.domain.model.WorldCupStadium
import kotlinx.coroutines.flow.Flow

interface StadiumRepository {
    fun observeStadiums(): Flow<List<WorldCupStadium>>
    suspend fun syncFromRemote()
}
