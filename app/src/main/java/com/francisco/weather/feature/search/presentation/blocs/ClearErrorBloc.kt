package com.francisco.weather.feature.search.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.feature.search.presentation.SearchEvent
import com.francisco.weather.feature.search.presentation.SearchState

class ClearErrorBloc : BaseBloc<SearchEvent.ClearError, SearchState>() {

    override suspend fun handleEvent(
        event: SearchEvent.ClearError,
        updateState: suspend ((SearchState) -> SearchState) -> Unit,
    ) {
        updateState { it.copy(errorRes = null) }
    }
}
