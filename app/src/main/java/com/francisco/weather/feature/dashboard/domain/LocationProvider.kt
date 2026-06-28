package com.francisco.weather.feature.dashboard.domain

import com.francisco.weather.feature.dashboard.domain.model.Coordinates

interface LocationProvider {
    /**
     * Returns device coordinates if a GPS fix is available (permission granted, GPS active,
     * location obtained); null if not. Callers should treat null as "use IP fallback".
     */
    suspend fun currentLocation(): Coordinates?
}
