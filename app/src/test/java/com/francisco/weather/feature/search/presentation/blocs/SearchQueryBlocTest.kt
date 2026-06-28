package com.francisco.weather.feature.search.presentation.blocs

import com.francisco.weather.feature.search.domain.usecase.SearchLocationsUseCase
import com.francisco.weather.feature.search.presentation.SearchEvent
import com.francisco.weather.feature.search.presentation.SearchState
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SearchQueryBlocTest {

    private lateinit var searchLocations: SearchLocationsUseCase
    private lateinit var bloc: SearchQueryBloc

    @Before
    fun setUp() {
        searchLocations = mockk(relaxed = true)
        bloc = SearchQueryBloc(searchLocations)
    }

    @Test
    fun `handleEvent reflects query in state immediately`() = runTest {
        var state = SearchState()

        bloc.handleEvent(
            event = SearchEvent.QueryChanged("London"),
            updateState = { reducer -> state = reducer(state) },
        )

        assertEquals("London", state.query)
    }

    @Test
    fun `handleEvent clears error in state`() = runTest {
        var state = SearchState(error = "previous error")

        bloc.handleEvent(
            event = SearchEvent.QueryChanged("Paris"),
            updateState = { reducer -> state = reducer(state) },
        )

        assertNull(state.error)
    }

    @Test
    fun `handleEvent forwards query to use case`() = runTest {
        var state = SearchState()

        bloc.handleEvent(
            event = SearchEvent.QueryChanged("Bogotá"),
            updateState = { reducer -> state = reducer(state) },
        )

        verify(exactly = 1) { searchLocations.setQuery("Bogotá") }
    }

    @Test
    fun `handleEvent forwards empty query to use case`() = runTest {
        var state = SearchState()

        bloc.handleEvent(
            event = SearchEvent.QueryChanged(""),
            updateState = { reducer -> state = reducer(state) },
        )

        verify(exactly = 1) { searchLocations.setQuery("") }
        assertEquals("", state.query)
    }
}
