package com.mirabilis.mvi.ui.presentation.main.signIn

import androidx.lifecycle.viewModelScope
import com.mirabilis.mvi.archicteture.MVIViewModel
import com.mirabilis.mvi.data.DataSource
import com.mirabilis.mvi.domain.Validator
import kotlinx.coroutines.launch

class SignInViewModel(private val dataSource: DataSource? = DataSource()) :
    MVIViewModel<SignIn.State, SignIn.Event, SignIn.Intent, SignIn.Effect>() {

    override fun getInitial(): SignIn.State = SignIn.State.initial()

    override fun onReduce(oldState: SignIn.State, event: SignIn.Event): SignIn.State =
        when (event) {
            is SignIn.Event.Loading -> oldState.copy(isLoading = event.value)
            is SignIn.Event.Error -> oldState.copy(error = event.error)
            is SignIn.Event.UpdateEmail -> {
                val email = event.value
                val valid = if (email.isEmpty()) null else Validator.Email.isValid(email)
                oldState.copy(email = email, isValidEmail = valid)
            }

            is SignIn.Event.UpdatePassword -> {
                val password = event.value
                val valid = if (password.isEmpty()) null else Validator.Password.isValid(password)
                oldState.copy(password = password, isValidPassword = valid)
            }

            is SignIn.Event.Clear -> SignIn.State.initial()
        }

    override fun onIntent(intent: SignIn.Intent) {
        when (intent) {
            SignIn.Intent.Submit -> submit()
            SignIn.Intent.OkError -> clear()
        }
    }

    private fun clear() {
        setEvent { SignIn.Event.Clear }
    }

    private fun submit() {
        viewModelScope.launch {
            setEvent { SignIn.Event.Loading(true) }
            try {
                dataSource?.signIn(state.value.email, state.value.password)
                setEffect { SignIn.Effect.OnNext }
            } catch (t: Throwable) {
                setEvent { SignIn.Event.Error(t) }
            } finally {
                setEvent { SignIn.Event.Loading(false) }
            }
        }
    }
}
