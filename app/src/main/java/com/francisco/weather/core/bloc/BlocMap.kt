package com.francisco.weather.core.bloc

import kotlin.reflect.KClass

/**
 * Type-safe map from [KClass] of an event to its [BaseBloc] handler.
 * Using an explicit delegate map avoids the Kotlin compiler ambiguity with
 * trailing-lambda syntax in a class `by` delegation header.
 */
class BlocMap<EventGroup : BaseEvent, State : BaseState>(
    entries: List<EventBlocItem<out EventGroup, State>>,
) {
    private val delegate: Map<KClass<out EventGroup>, BaseBloc<out EventGroup, State>> =
        entries.associate { it.event to it.baseBloc }

    operator fun get(key: KClass<out EventGroup>): BaseBloc<out EventGroup, State>? =
        delegate[key]
}

data class EventBlocItem<Event : BaseEvent, State : BaseState>(
    val event: KClass<Event>,
    val baseBloc: BaseBloc<Event, State>,
)

infix fun <Event : BaseEvent, State : BaseState> KClass<Event>.with(
    block: BaseBloc<Event, State>,
): EventBlocItem<Event, State> = EventBlocItem(event = this, baseBloc = block)

fun <EventGroup : BaseEvent, State : BaseState> blocMapOf(
    vararg entries: EventBlocItem<out EventGroup, State>,
): BlocMap<EventGroup, State> = BlocMap(entries.toList())
