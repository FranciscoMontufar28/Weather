package com.francisco.weather.feature.search.presentation.blocs

import com.francisco.weather.feature.search.domain.SearchRepository
import com.francisco.weather.feature.search.domain.model.Location
import com.francisco.weather.feature.search.presentation.SearchEvent
import com.francisco.weather.feature.search.presentation.SearchState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchQueryBlocTest {

    private lateinit var repository: SearchRepository
    private lateinit var bloc: SearchQueryBloc

    private val testLocations = listOf(
        Location(id = 1, name = "Bogotá", region = "Bogota D.C.", country = "Colombia"),
    )

    @Before
    fun setUp() {
        repository = mockk()
        bloc = SearchQueryBloc(repository)
    }

    @Test
    fun `empty query clears locations without calling API`() = runTest {
        var state = SearchState(query = "prev", locations = testLocations)

        bloc.handleEvent(
            event = SearchEvent.QueryChanged(""),
            updateState = { reducer -> state = reducer(state) },
        )

        assertTrue(state.locations.isEmpty())
        assertFalse(state.isLoading)
        coVerify(exactly = 0) { repository.search(any()) }
    }

    @Test
    fun `non-empty query calls API after debounce and updates state`() = runTest(StandardTestDispatcher()) {
        var state = SearchState()
        coEvery { repository.search("Col") } returns Result.success(testLocations)

        // Launch bloc event — it will suspend at delay(300)
        val job = launch {
            bloc.handleEvent(
                event = SearchEvent.QueryChanged("Col"),
                updateState = { reducer -> state = reducer(state) },
            )
        }

        // Advance past debounce threshold
        advanceTimeBy(400)
        job.join()

        assertEquals(testLocations, state.locations)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `repository failure sets error message in state`() = runTest {
        var state = SearchState()
        coEvery { repository.search("fail") } returns Result.failure(RuntimeException("boom"))

        bloc.handleEvent(
            event = SearchEvent.QueryChanged("fail"),
            updateState = { reducer -> state = reducer(state) },
        )

        assertFalse(state.isLoading)
        assertTrue(state.locations.isEmpty())
        assertEquals("boom", state.error)
    }

    @Test
    fun `blank query (spaces only) does not call API`() = runTest {
        var state = SearchState()
        coEvery { repository.search(any()) } returns Result.success(testLocations)

        bloc.handleEvent(
            event = SearchEvent.QueryChanged("   "),
            updateState = { reducer -> state = reducer(state) },
        )

        coVerify(exactly = 0) { repository.search(any()) }
        assertTrue(state.locations.isEmpty())
    }
}
