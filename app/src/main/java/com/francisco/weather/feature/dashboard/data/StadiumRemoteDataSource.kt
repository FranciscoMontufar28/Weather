package com.francisco.weather.feature.dashboard.data

import com.francisco.weather.feature.dashboard.domain.model.WorldCupStadium
import kotlinx.coroutines.delay
import javax.inject.Inject

class StadiumRemoteDataSource @Inject constructor() {

    /** Simulates a backend call — returns the 16 FIFA World Cup 2026 venues. */
    suspend fun fetch(): List<WorldCupStadium> {
        delay(400) // simulate network latency
        return worldCup2026Stadiums
    }

    private val worldCup2026Stadiums: List<WorldCupStadium> = listOf(
        WorldCupStadium("Mercedes-Benz Stadium", "Atlanta", "Estados Unidos", "US", 33.7553, -84.4006, "https://picsum.photos/seed/mercedes-benz-stadium/400/300"),
        WorldCupStadium("Gillette Stadium", "Boston", "Estados Unidos", "US", 42.0909, -71.2643, "https://picsum.photos/seed/gillette-stadium/400/300"),
        WorldCupStadium("AT&T Stadium", "Dallas", "Estados Unidos", "US", 32.7473, -97.0945, "https://picsum.photos/seed/att-stadium/400/300"),
        WorldCupStadium("Estadio Akron", "Guadalajara", "México", "MX", 20.6817, -103.4625, "https://picsum.photos/seed/estadio-akron/400/300"),
        WorldCupStadium("NRG Stadium", "Houston", "Estados Unidos", "US", 29.6847, -95.4107, "https://picsum.photos/seed/nrg-stadium/400/300"),
        WorldCupStadium("Arrowhead Stadium", "Kansas City", "Estados Unidos", "US", 39.0489, -94.4839, "https://picsum.photos/seed/arrowhead-stadium/400/300"),
        WorldCupStadium("SoFi Stadium", "Los Ángeles", "Estados Unidos", "US", 33.9535, -118.3392, "https://picsum.photos/seed/sofi-stadium/400/300"),
        WorldCupStadium("Estadio Azteca", "Ciudad de México", "México", "MX", 19.3031, -99.1506, "https://picsum.photos/seed/estadio-azteca/400/300"),
        WorldCupStadium("Hard Rock Stadium", "Miami", "Estados Unidos", "US", 25.9580, -80.2389, "https://picsum.photos/seed/hard-rock-stadium/400/300"),
        WorldCupStadium("Estadio BBVA", "Monterrey", "México", "MX", 25.6694, -100.2444, "https://picsum.photos/seed/estadio-bbva/400/300"),
        WorldCupStadium("MetLife Stadium", "Nueva York/Nueva Jersey", "Estados Unidos", "US", 40.8135, -74.0745, "https://picsum.photos/seed/metlife-stadium/400/300"),
        WorldCupStadium("Lincoln Financial Field", "Filadelfia", "Estados Unidos", "US", 39.9008, -75.1675, "https://picsum.photos/seed/lincoln-financial-field/400/300"),
        WorldCupStadium("Levi's Stadium", "San Francisco", "Estados Unidos", "US", 37.4030, -121.9698, "https://picsum.photos/seed/levis-stadium/400/300"),
        WorldCupStadium("Lumen Field", "Seattle", "Estados Unidos", "US", 47.5952, -122.3316, "https://picsum.photos/seed/lumen-field/400/300"),
        WorldCupStadium("BMO Field", "Toronto", "Canadá", "CA", 43.6332, -79.4185, "https://picsum.photos/seed/bmo-field/400/300"),
        WorldCupStadium("BC Place", "Vancouver", "Canadá", "CA", 49.2768, -123.1119, "https://picsum.photos/seed/bc-place/400/300"),
    )
}
