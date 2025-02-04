package com.mirabilis.mvi.ui.presentation.main.signup

import androidx.lifecycle.viewModelScope
import com.mirabilis.mvi.archicteture.CMVIViewModel
import com.mirabilis.mvi.data.DataSource
import com.mirabilis.mvi.domain.Validator
import kotlinx.coroutines.launch

class SignUpViewModel(private val dataSource: DataSource? = DataSource()) :
    CMVIViewModel<SignUp.State, SignUp.Intent, SignUp.Effect>() {

    override fun getInitial(): SignUp.State = SignUp.State.initial()

    override fun sendIntent(intent: SignUp.Intent) {
        when (intent) {
            is SignUp.Intent.DoOkError -> clear()
            is SignUp.Intent.DoSubmit -> submit()

            is SignUp.Intent.OnUpdateEmail -> {
                val email = intent.value
                val valid = if (email.isEmpty()) null else Validator.Email.isValid(email)
                updateState(state.value.copy(email = email, isValidEmail = valid))
            }

            is SignUp.Intent.OnUpdatePassword -> {
                val password = intent.value
                val valid = if (password.isEmpty()) null else Validator.Password.isValid(password)
                updateState(state.value.copy(password = password, isValidPassword = valid))
            }

            is SignUp.Intent.OnUpdateConfirmPassword -> {
                val confirmPassword = intent.value
                val valid = if (confirmPassword.isEmpty()) null else Validator.Password.isConfirmed(
                    state.value.password,
                    confirmPassword
                )
                updateState(
                    state.value.copy(
                        confirmPassword = confirmPassword,
                        isValidConfirmPassword = valid
                    )
                )
            }
        }
    }

    private fun clear() {
        updateState(SignUp.State.initial())
    }

    private fun submit() {
        viewModelScope.launch {
            updateState(state.value.copy(isLoading = true))
            try {
                dataSource?.signUp(state.value.email, state.value.password)
                sendEffect { SignUp.Effect.Success }
            } catch (t: Throwable) {
                updateState(state.value.copy(error = t))
            } finally {
                updateState(state.value.copy(isLoading = false))
            }
        }
    }
}