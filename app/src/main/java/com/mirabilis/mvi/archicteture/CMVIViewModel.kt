package com.mirabilis.mvi.archicteture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Compact MVI View Model: This is a Compact implementation from MVI.
 * Everything is intent, including changes from components, user interactions,, and others.
 */
abstract class CMVIViewModel<STATE : UiState, INTENT : UiIntent, EFFECT : UiEffect> : ViewModel() {

    protected abstract fun getInitial(): STATE
    abstract fun sendIntent(intent: INTENT)

    private val _state: MutableStateFlow<STATE> by lazy { MutableStateFlow(getInitial()) }
    val state: StateFlow<STATE> get() = _state

    private val _effects: MutableSharedFlow<EFFECT> = MutableSharedFlow()
    val effects = _effects.asSharedFlow()

    protected fun updateState(newState: STATE) {
        _state.tryEmit(newState)
    }

    fun sendEffect(builder: () -> EFFECT) {
        val effect = builder()
        viewModelScope.launch { _effects.emit(effect) }
    }
}