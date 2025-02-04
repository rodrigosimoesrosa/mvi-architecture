package com.mirabilis.mvi.ui.presentation.main.signIn

import androidx.compose.runtime.Immutable
import com.mirabilis.mvi.archicteture.UiEffect
import com.mirabilis.mvi.archicteture.UiEvent
import com.mirabilis.mvi.archicteture.UiIntent
import com.mirabilis.mvi.archicteture.UiState

object SignIn {

    @Immutable
    sealed interface Effect : UiEffect {
        data object OnNext : Effect
    }

    @Immutable
    sealed interface Event : UiEvent {
        data class UpdateEmail(val value: String) : Event
        data class UpdatePassword(val value: String) : Event
        data class Loading(val value: Boolean) : Event
        data class Error(val error: Throwable?) : Event
        data object Clear : Event
    }

    @Immutable
    sealed interface Intent : UiIntent {
        data object Submit : Intent
        data object OkError : Intent
    }

    @Immutable
    data class State(
        val isLoading: Boolean,
        val error: Throwable?,
        val email: String,
        val isValidEmail: Boolean?,
        val password: String,
        val isValidPassword: Boolean?
    ) : UiState {

        fun canSubmit(): Boolean = isValidEmail == true && isValidPassword == true

        companion object {
            fun initial() = State(
                error = null,
                isLoading = false,
                email = "",
                isValidEmail = null,
                password = "",
                isValidPassword = null
            )
        }
    }
}