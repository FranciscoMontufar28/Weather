package com.francisco.weather.feature.search.presentation

import com.francisco.weather.core.bloc.BaseBlocFactory
import com.francisco.weather.core.bloc.blocMapOf
import com.francisco.weather.core.bloc.with
import com.francisco.weather.feature.search.presentation.blocs.ClearErrorBloc
import com.francisco.weather.feature.search.presentation.blocs.RecordRecentBloc
import com.francisco.weather.feature.search.presentation.blocs.SearchQueryBloc

class SearchBlocFactory(
    searchQueryBloc: SearchQueryBloc,
    recordRecentBloc: RecordRecentBloc,
) : BaseBlocFactory<SearchEvent, SearchState>() {

    private val clearErrorBloc = ClearErrorBloc()

    override val blocs = blocMapOf(
        SearchEvent.QueryChanged::class with searchQueryBloc,
        SearchEvent.ClearError::class with clearErrorBloc,
        SearchEvent.LocationSelected::class with recordRecentBloc,
    )
}
