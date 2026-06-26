package com.francisco.weather.core.bloc

import kotlin.reflect.KClass

abstract class BaseBlocFactory<EventGroup : BaseEvent, State : BaseState> {

    protected open val blocs: BlocMap<EventGroup, State> = blocMapOf()

    fun getBlocByEvent(event: EventGroup): BaseBloc<out EventGroup, State>? = blocs[event::class]
}
