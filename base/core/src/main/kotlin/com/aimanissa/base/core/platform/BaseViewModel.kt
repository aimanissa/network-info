package com.aimanissa.base.core.platform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Одиночные ивенты: например для отображения диалога
 */
interface SingleEvent

/**
 * Ивенты, с которыми работает VM
 */
interface Event

/**
 * Ивенты, которые летят от View
 */
interface UiEvent : Event

/**
 * Ивенты, которые летят внутри VM
 */
interface DataEvent : Event

/**
 * Ивенты, которые летают между несколькими VM
 */
interface OutputEvent : Event

/**
 * Ивенты, которые летают между несколькими VM
 */
interface InputEvent : Event

/**
 * Ивенты для обработки ошибок
 */
interface ErrorEvent : Event {
    val error: Throwable
}

open class BaseViewState {
    var afterChangedStateAction: (() -> Unit)? = null
}

@Suppress("TooManyFunctions")
abstract class BaseViewModel<VIEW_STATE : BaseViewState>(
    private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _viewState: MutableStateFlow<VIEW_STATE> by lazy { MutableStateFlow(initialViewState()) }
    val viewState: StateFlow<VIEW_STATE>
        get() = _viewState.asStateFlow()

    protected val previousState: VIEW_STATE
        get() = viewState.value

    private val _singleEvent: Channel<SingleEvent> = Channel()
    val singleEvent = _singleEvent.receiveAsFlow()

    protected abstract fun initialViewState(): VIEW_STATE

    protected abstract fun reduce(event: Event): VIEW_STATE

    fun processUiEvent(event: UiEvent) {
        updateState(event)
    }

    protected fun processDataEvent(event: DataEvent) {
        updateState(event)
    }

    protected fun processOutputEvent(event: OutputEvent) {
        updateState(event)
    }

    protected fun processInputEvent(event: InputEvent) {
        updateState(event)
    }

    protected fun processErrorEvent(event: ErrorEvent) {
        handleErrorEvent(event)
    }

    protected fun sendSingleEvent(event: SingleEvent, actionAfter: () -> Unit = {}) {
        viewModelScope.launch(dispatcher) {
            _singleEvent.send(event)
            actionAfter()
        }
    }

    private fun updateState(event: Event) {
        val newViewState = reduce(event)
        compareNewStateWithCurrentAndUpdate(newViewState)
    }

    private fun compareNewStateWithCurrentAndUpdate(newViewState: VIEW_STATE) {
        if (newViewState != viewState.value) {
            _viewState.update { newViewState }
            newViewState.afterChangedStateAction?.invoke()
        }
    }

    private fun handleErrorEvent(event: ErrorEvent) {
        val message = event.error.message
        if (message.isNullOrEmpty().not()) {
            Timber.e(message)
        }
        updateState(event)
    }

    fun VIEW_STATE.afterAction(action: () -> Unit): VIEW_STATE {
        afterChangedStateAction = action
        return this
    }
}
