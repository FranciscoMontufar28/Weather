package com.francisco.weather.core.bloc

abstract class BaseBloc<Event : BaseEvent, State : BaseState> {

    abstract val tag: String

    @Suppress("UNCHECKED_CAST")
    suspend fun run(event: Any, updateState: suspend ((State) -> State) -> Unit) {
        try {
            val castedEvent = event as? Event ?: return
            handleEvent(castedEvent, updateState)
        } catch (_: ClassCastException) {
        }
    }

    abstract suspend fun handleEvent(
        event: Event,
        updateState: suspend ((State) -> State) -> Unit,
    )
}
