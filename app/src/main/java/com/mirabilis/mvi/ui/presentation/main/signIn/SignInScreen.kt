package com.mirabilis.mvi.ui.presentation.main.signIn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirabilis.mvi.R
import com.mirabilis.mvi.ui.InputPassword
import com.mirabilis.mvi.ui.InputText
import com.mirabilis.mvi.ui.components.ErrorDialog
import com.mirabilis.mvi.ui.components.LoadingDialog
import com.mirabilis.mvi.ui.theme.MVITheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignInScreen(onNext: () -> Unit) {
    val viewModel = SignInViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                SignIn.Effect.OnNext -> onNext()
            }
        }
    }

    MVITheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            SignInContent(
                modifier = Modifier.padding(innerPadding),
                onState = { state },
                onIntent = { intent -> viewModel.setIntent { intent } },
                onEvent = { event -> viewModel.setEvent { event } }
            )
        }
    }
}

@Composable
fun SignInContent(
    modifier: Modifier,
    onState: () -> SignIn.State,
    onIntent: (SignIn.Intent) -> Unit,
    onEvent: (SignIn.Event) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    if (onState().isLoading) LoadingDialog()
    if (onState().error != null) {
        ErrorDialog(
            title = R.string.sign_in_an_error_title,
            subTitle = R.string.sign_in_an_error_subtitle,
            onConfirm = {
                onIntent(SignIn.Intent.OkError)
                focusRequester.requestFocus()
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = modifier
                .fillMaxHeight(.8f)
                .padding(start = 16.dp, end = 16.dp)
        ) {
            val userNameError =
                if (onState().isValidEmail == false) R.string.sign_in_invalid_email
                else null

            InputText(
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                value = onState().email,
                onValueChange = { onEvent(SignIn.Event.UpdateEmail(it)) },
                error = userNameError,
                label = R.string.sign_in_email,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            val passwordError =
                if (onState().isValidPassword == false) R.string.sign_in_invalid_password
                else null

            InputPassword(
                modifier = Modifier.fillMaxWidth(),
                value = onState().password,
                onValueChange = { onEvent(SignIn.Event.UpdatePassword(it)) },
                label = R.string.sign_in_password,
                error = passwordError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                onSubmit = { onIntent(SignIn.Intent.Submit) }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.Absolute.Right,
            verticalAlignment = Alignment.Bottom
        ) {
            Button(
                enabled = onState().canSubmit(),
                onClick = { onIntent(SignIn.Intent.Submit) }
            ) {
                Text(
                    text = stringResource(R.string.sign_in_button),
                    style = TextStyle(
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    )
                )
            }
        }
    }
}

@Composable
@Preview
fun SignInScreenPreview(state: SignIn.State = SignIn.State.initial()) {
    MVITheme {
        Surface {
            SignInContent(Modifier, { state }, {}, {})
        }
    }
}