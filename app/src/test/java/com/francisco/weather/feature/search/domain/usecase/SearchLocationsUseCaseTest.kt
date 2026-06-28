package com.francisco.weather.feature.search.domain.usecase

import com.francisco.weather.feature.search.domain.SearchRepository
import com.francisco.weather.feature.search.domain.model.Location
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchLocationsUseCaseTest {

    private lateinit var repository: SearchRepository
    private lateinit var useCase: SearchLocationsUseCase

    private val bogota = Location(id = 1, name = "Bogotá", region = "Bogota D.C.", country = "Colombia")
    private val london = Location(id = 2, name = "London", region = "England", country = "UK")

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SearchLocationsUseCase(repository)
    }

    @Test
    fun `blank query emits Empty without calling repository`() = runTest(StandardTestDispatcher()) {
        val results = mutableListOf<SearchResults>()
        val job = launch { useCase().toList(results) }

        // Default query is "" — after debounce emits Empty
        advanceTimeBy(400)
        job.cancel()

        assertTrue(results.any { it is SearchResults.Empty })
        coVerify(exactly = 0) { repository.search(any()) }
    }

    @Test
    fun `non-blank query emits Loading then Success after debounce`() = runTest(StandardTestDispatcher()) {
        coEvery { repository.search("Bog") } returns Result.success(listOf(bogota))

        val results = mutableListOf<SearchResults>()
        val job = launch { useCase().toList(results) }

        useCase.setQuery("Bog")
        advanceTimeBy(400)
        job.cancel()

        assertTrue(results.contains(SearchResults.Loading))
        assertTrue(results.contains(SearchResults.Success(listOf(bogota))))
    }

    @Test
    fun `two rapid queries — flatMapLatest cancels first, only last resolves`() = runTest(StandardTestDispatcher()) {
        coEvery { repository.search("Lo") } returns Result.success(listOf(london))
        coEvery { repository.search("London") } returns Result.success(listOf(london))

        val results = mutableListOf<SearchResults>()
        val job = launch { useCase().toList(results) }

        useCase.setQuery("Lo")
        advanceTimeBy(100)          // antes del debounce: "Lo" no ha llegado al repo
        useCase.setQuery("London")  // cancela "Lo", reinicia debounce
        advanceTimeBy(400)
        job.cancel()

        // "Lo" no debería haber llegado al repositorio
        coVerify(exactly = 0) { repository.search("Lo") }
        coVerify(exactly = 1) { repository.search("London") }
        assertTrue(results.contains(SearchResults.Success(listOf(london))))
    }

    @Test
    fun `repository failure emits Error`() = runTest(StandardTestDispatcher()) {
        coEvery { repository.search("fail") } returns Result.failure(RuntimeException("boom"))

        val results = mutableListOf<SearchResults>()
        val job = launch { useCase().toList(results) }

        useCase.setQuery("fail")
        advanceTimeBy(400)
        job.cancel()

        assertTrue(results.any { it is SearchResults.Error && it.message == "boom" })
    }

    @Test
    fun `repeated identical queries — distinctUntilChanged prevents duplicate calls`() = runTest(StandardTestDispatcher()) {
        coEvery { repository.search("Paris") } returns Result.success(emptyList())

        val results = mutableListOf<SearchResults>()
        val job = launch { useCase().toList(results) }

        useCase.setQuery("Paris")
        advanceTimeBy(400)
        useCase.setQuery("Paris") // same — should not trigger another search
        advanceTimeBy(400)
        job.cancel()

        coVerify(exactly = 1) { repository.search("Paris") }
    }
}
