package com.mirabilis.mvi.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mirabilis.mvi.R
import com.mirabilis.mvi.ui.theme.MVITheme

@Composable
fun InputText(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes label: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxLines: Int = 1,
    singleLine: Boolean = maxLines == 1,
    trailingIcon: ImageVector? = null,
    @StringRes error: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onSubmit: (() -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = modifier.focusRequester(focusRequester),
        value = value,
        onValueChange = { onValueChange(it) },
        enabled = enabled,
        isError = error != null,
        maxLines = maxLines,
        singleLine = singleLine,
        label = { Text(stringResource(id = label)) },
        supportingText = {
            if (error != null) {
                Text(text = stringResource(id = error))
            }
        },
        trailingIcon = {
            if (trailingIcon != null) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    imageVector = trailingIcon,
                    contentDescription = null
                )
            }
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onDone = { onSubmit?.invoke() },
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        )
    )
}

@Composable
fun InputPassword(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes label: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxLines: Int = 1,
    singleLine: Boolean = maxLines == 1,
    @StringRes error: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onSubmit: (() -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = modifier.focusRequester(focusRequester),
        value = value,
        onValueChange = { onValueChange(it) },
        enabled = enabled,
        isError = error != null,
        maxLines = maxLines,
        singleLine = singleLine,
        label = { Text(stringResource(id = label)) },
        supportingText = {
            if (error != null) {
                Text(text = stringResource(id = error))
            }
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onDone = { onSubmit?.invoke() },
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        )
    )
}

@Composable
@Preview
fun InputTextPreview() {
    MVITheme {
        Surface {
            InputText(
                modifier = Modifier,
                value = "",
                onValueChange = {},
                label = R.string.sign_in_email,
                trailingIcon = Icons.Default.Email
            )
        }
    }
}

@Composable
@Preview
fun InputPasswordPreview() {
    MVITheme {
        Surface {
            InputPassword(
                modifier = Modifier,
                value = "aaaa",
                onValueChange = {},
                label = R.string.sign_in_password,
            )
        }
    }
}
