package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.core.domain.recent.RecentSearch
import com.francisco.weather.feature.dashboard.domain.usecase.ObserveRecentSearchesUseCase
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveRecentsBlocTest {

    private val sampleRecents = listOf(
        RecentSearch(name = "Bogotá", region = "Bogota D.C.", country = "Colombia"),
        RecentSearch(name = "London", region = "England", country = "UK"),
    )

    @Test
    fun `flow emission updates recentSearches in state`() = runTest {
        val observeRecents = mockk<ObserveRecentSearchesUseCase>()
        every { observeRecents() } returns flowOf(sampleRecents)

        val bloc = ObserveRecentsBloc(observeRecents)
        var state = DashboardState()
        bloc.handleEvent(
            event = DashboardEvent.ObserveRecents,
            updateState = { reducer -> state = reducer(state) },
        )

        assertEquals(sampleRecents, state.recentSearches)
    }

    @Test
    fun `empty emission clears recentSearches`() = runTest {
        val observeRecents = mockk<ObserveRecentSearchesUseCase>()
        every { observeRecents() } returns flowOf(emptyList())

        val bloc = ObserveRecentsBloc(observeRecents)
        var state = DashboardState(recentSearches = sampleRecents)
        bloc.handleEvent(
            event = DashboardEvent.ObserveRecents,
            updateState = { reducer -> state = reducer(state) },
        )

        assertEquals(emptyList<RecentSearch>(), state.recentSearches)
    }
}
