package com.mirabilis.mvi.archicteture

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * MVI Reducer: Reducer is a component responsible for handling events and updating the state of the application in a predictable and immutable way.
 * Immutability: The state is updated immutably, ensuring predictability and avoiding side effects.
 * Centralized Logic: All state transitions are handled in one place (reduce method), making the code easier to debug and maintain.
 * Unidirectional Flow: Events trigger state updates, and the UI reacts to state changes, creating a clear and consistent data flow.
 */
abstract class MVIReducer<STATE : UiState, EVENT : UiEvent>(state: STATE) {

    private val _state = MutableStateFlow(state)
    val state: StateFlow<STATE> = _state

    abstract fun reduce(oldState: STATE, event: EVENT)

    fun sendEvent(event: EVENT) {
        reduce(_state.value, event)
    }

    fun setState(newState: STATE): Boolean {
        return _state.tryEmit(newState)
    }
}
