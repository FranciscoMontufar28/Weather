package com.francisco.weather.core.bloc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BlocViewModel<Event : BaseEvent, State : BaseState>(
    initialState: State,
) : ViewModel() {

    protected val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    protected abstract val factory: BaseBlocFactory<Event, State>

    /**
     * Dispatches [event] to its corresponding [BaseBloc].
     * Returns Unit so subclasses can override without return-type conflicts.
     */
    open fun onEvent(event: Event) {
        viewModelScope.launch {
            val bloc = factory.getBlocByEvent(event) ?: return@launch
            try {
                bloc.run(event, ::safeUpdateState)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    protected suspend fun safeUpdateState(newState: (State) -> State) {
        withContext(Dispatchers.Main) { _state.update { newState(it) } }
    }

    protected open fun handleError(e: Exception) {}
}
