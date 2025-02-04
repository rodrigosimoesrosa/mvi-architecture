package com.mirabilis.mvi.archicteture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirabilis.mvi.log.logDebug
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * MVI (Model-View-Intent) is a unidirectional architecture pattern where the View emits Intents (user actions), the Model represents the state of the application, and the ViewModel processes the intents to update the state, which is then reflected back in the View, ensuring a predictable and maintainable flow of data.
 * STATE: Represents the current state of the screen, containing all the necessary data to render the UI.
 * EVENT: Represents an intention to change the state, often triggered by UI components or system events. It may not always directly correspond to user interactions.
 * INTENT: Represents the user's explicit intention to interact with the screen, such as clicking a button.
 * EFFECT: A stream responsible for handling side effects or actions that occur outside the UI context, such as displaying toasts, navigating to other screens, or triggering one-time events.
 */
abstract class MVIViewModel<STATE : UiState, EVENT : UiEvent, INTENT : UiIntent, EFFECT : UiEffect> :
    ViewModel() {

    protected abstract fun getInitial(): STATE
    protected abstract fun onIntent(intent: INTENT)
    protected abstract fun onReduce(oldState: STATE, event: EVENT): STATE

    private val reducer: MVIReducer<STATE, EVENT> by lazy {
        object : MVIReducer<STATE, EVENT>(getInitial()) {
            override fun reduce(oldState: STATE, event: EVENT) {
                setState(this@MVIViewModel.onReduce(oldState, event))
            }
        }
    }

    val state: StateFlow<STATE>
        get() = reducer.state

    private val _events = Channel<EVENT>(Channel.BUFFERED)
    private val events = _events.receiveAsFlow()

    private val _intents: MutableSharedFlow<INTENT> = MutableSharedFlow()
    private val intents = _intents.asSharedFlow()

    private val _effects: MutableSharedFlow<EFFECT> = MutableSharedFlow()
    val effects = _effects.asSharedFlow()

    init {
        subscribeIntents()
        subscribeEvents()

        state.onEach { logDebug("State: $it") }.launchIn(viewModelScope)
        intents.onEach { logDebug("Intent: $it") }.launchIn(viewModelScope)
        effects.onEach { logDebug("Effect: $it") }.launchIn(viewModelScope)
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            events.collect { reducer.sendEvent(it) }
        }
    }

    private fun subscribeIntents() {
        viewModelScope.launch {
            intents.collect { onIntent(it) }
        }
    }

    fun setEvent(builder: () -> EVENT) {
        val event = builder()
        viewModelScope.launch {
            val delivered = _events.trySend(event).isSuccess
            if (!delivered) {
                error("Missed event $event!")
            }
        }
    }

    fun setEffect(builder: () -> EFFECT) {
        val effect = builder()
        viewModelScope.launch { _effects.emit(effect) }
    }

    fun setIntent(builder: () -> INTENT) {
        val intent = builder()
        viewModelScope.launch { _intents.emit(intent) }
    }
}