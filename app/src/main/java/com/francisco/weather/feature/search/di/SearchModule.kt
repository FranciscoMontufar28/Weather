package com.francisco.weather.feature.search.di

import com.francisco.weather.core.domain.recent.RecentSearchRepository
import com.francisco.weather.feature.search.data.SearchRepositoryImpl
import com.francisco.weather.feature.search.domain.SearchRepository
import com.francisco.weather.feature.search.presentation.SearchBlocFactory
import com.francisco.weather.feature.search.presentation.blocs.RecordRecentBloc
import com.francisco.weather.feature.search.presentation.blocs.SearchQueryBloc
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class SearchModule {

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    companion object {

        @Provides
        @Singleton
        fun provideSearchQueryBloc(repository: SearchRepository): SearchQueryBloc =
            SearchQueryBloc(repository)

        @Provides
        @Singleton
        fun provideRecordRecentBloc(recentRepository: RecentSearchRepository): RecordRecentBloc =
            RecordRecentBloc(recentRepository)

        @Provides
        @Singleton
        fun provideSearchBlocFactory(
            searchQueryBloc: SearchQueryBloc,
            recordRecentBloc: RecordRecentBloc,
        ): SearchBlocFactory = SearchBlocFactory(searchQueryBloc, recordRecentBloc)
    }
}
