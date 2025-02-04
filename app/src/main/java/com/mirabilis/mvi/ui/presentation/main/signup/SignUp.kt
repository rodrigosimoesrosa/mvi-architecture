package com.mirabilis.mvi.ui.presentation.main.signup

import androidx.compose.runtime.Immutable
import com.mirabilis.mvi.archicteture.UiEffect
import com.mirabilis.mvi.archicteture.UiIntent
import com.mirabilis.mvi.archicteture.UiState

object SignUp {

    @Immutable
    sealed interface Effect : UiEffect {
        data object Success : Effect
    }

    @Immutable
    sealed interface Intent : UiIntent {
        data object DoSubmit : Intent
        data object DoOkError : Intent

        data class OnUpdateEmail(val value: String) : Intent
        data class OnUpdatePassword(val value: String) : Intent
        data class OnUpdateConfirmPassword(val value: String) : Intent
    }

    @Immutable
    data class State(
        val isLoading: Boolean,
        val error: Throwable?,
        val email: String,
        val isValidEmail: Boolean?,
        val password: String,
        val isValidPassword: Boolean?,
        val confirmPassword: String,
        val isValidConfirmPassword: Boolean?
    ) : UiState {

        fun canSubmit(): Boolean =
            isValidEmail == true && isValidPassword == true && isValidConfirmPassword == true

        companion object {
            fun initial() = State(
                error = null,
                isLoading = false,
                email = "",
                isValidEmail = null,
                password = "",
                isValidPassword = null,
                confirmPassword = "",
                isValidConfirmPassword = null
            )
        }
    }
}