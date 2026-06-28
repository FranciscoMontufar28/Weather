package com.francisco.weather.feature.dashboard.domain

import com.francisco.weather.feature.dashboard.domain.model.WorldCupStadium
import kotlinx.coroutines.flow.Flow

interface StadiumRepository {
    fun observeStadiums(): Flow<List<WorldCupStadium>>
    /**
     * @param force when true, bypass the 5-minute TTL guard so weather data is re-fetched
     *              immediately (e.g. after a language change to refresh [conditionText]).
     */
    suspend fun syncFromRemote(force: Boolean = false)
}
