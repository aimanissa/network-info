package com.aimanissa.base.core.platform

import androidx.annotation.MainThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import com.aimanissa.base.core.platform.ViewModelsBus.EventsListenerSubscription

/**
 * Содержит классы для общения ViewModel'ей и только между собой.
 */
@Suppress("UnnecessaryAbstractClass", "unused")
object ViewModelsBus {

    abstract class BaseOutput<OUTPUT_EVENT : Event> {

        private val subscribers = arrayListOf<EventsListener<OUTPUT_EVENT>>()

        @MainThread
        fun push(outputEvent: OUTPUT_EVENT) {
            subscribers.forEach { it.onNewEvent(outputEvent) }
        }

        @MainThread
        fun subscribe(
            outputsSubscriptions: OutputsSubscriptions,
            func: (OUTPUT_EVENT) -> Unit
        ): EventsListenerSubscription {
            subscribers.add(func)
            return EventsListenerSubscription {
                subscribers.remove(func)
            }.also { outputsSubscriptions.putSubscription(it) }
        }

        @MainThread
        fun subscribe(func: (OUTPUT_EVENT) -> Unit): EventsListenerSubscription {
            subscribers.add(func)
            return EventsListenerSubscription {
                subscribers.remove(func)
            }
        }
    }

    abstract class BaseInput<INPUT_EVENT : Event> {
        private val _input = MutableSharedFlow<INPUT_EVENT?>(1)
        val input: Flow<INPUT_EVENT> get() = _input.filterNotNull()

        fun subscribe(viewModelScope: CoroutineScope, onNewEvent: (event: INPUT_EVENT) -> Unit) {
            viewModelScope.launch {
                input.collect { onNewEvent(it) }
            }
        }

        fun push(inputEvent: INPUT_EVENT) {
            _input.tryEmit(inputEvent)
        }

        fun clear() {
            _input.tryEmit(null)
        }
    }

    fun interface EventsListener<EVENT : Event> {
        fun onNewEvent(event: EVENT)
    }

    fun interface EventsListenerSubscription {
        @MainThread
        fun unsubscribe()
    }

    class OutputsSubscriptions {
        private val subscriptions = arrayListOf<EventsListenerSubscription>()

        @MainThread
        fun putSubscription(subscription: EventsListenerSubscription) {
            subscriptions.add(subscription)
        }

        @MainThread
        fun unsubscribe() {
            subscriptions.forEach { it.unsubscribe() }
            subscriptions.clear()
        }
    }
}
