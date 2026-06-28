package com.francisco.weather.feature.search.domain.usecase

import com.francisco.weather.R
import com.francisco.weather.core.network.toErrorRes
import timber.log.Timber
import com.francisco.weather.feature.search.domain.SearchRepository
import com.francisco.weather.feature.search.domain.model.Location
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchLocationsUseCase @Inject constructor(
    private val repository: SearchRepository,
) {
    private val queries = MutableStateFlow("")

    fun setQuery(query: String) {
        queries.value = query
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    operator fun invoke(): Flow<SearchResults> =
        queries
            .map(String::trim)
            .distinctUntilChanged()
            .debounce(300L)
            .flatMapLatest { q ->
                if (q.isBlank()) {
                    flowOf<SearchResults>(SearchResults.Empty)
                } else {
                    flow {
                        emit(SearchResults.Loading)
                        emit(repository.search(q).toResults())
                    }
                }
            }

    private fun Result<List<Location>>.toResults(): SearchResults =
        fold(
            onSuccess  = { SearchResults.Success(it) },
            onFailure  = {
                Timber.e(it, "Search locations failed")
                SearchResults.Error(it.toErrorRes(R.string.error_unexpected))
            },
        )
}
