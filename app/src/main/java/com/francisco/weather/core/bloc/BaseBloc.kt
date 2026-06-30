package com.francisco.weather.core.bloc

import kotlinx.coroutines.CancellationException
import timber.log.Timber

abstract class BaseBloc<Event : BaseEvent, State : BaseState> {
    @Suppress("UNCHECKED_CAST")
    suspend fun run(event: Any, updateState: suspend ((State) -> State) -> Unit) {
        val castedEvent = event as? Event ?: return
        try {
            handleEvent(castedEvent, updateState)
        } catch (_: ClassCastException) {
            // Subtipo de evento equivocado por type erasure — ignorar silenciosamente.
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Error handling %s", castedEvent::class.simpleName)
        }
    }

    abstract suspend fun handleEvent(
        event: Event,
        updateState: suspend ((State) -> State) -> Unit,
    )
}
