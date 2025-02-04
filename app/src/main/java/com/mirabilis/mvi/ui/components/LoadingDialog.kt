package com.mirabilis.mvi.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mirabilis.mvi.R
import com.mirabilis.mvi.ui.theme.MVITheme

@Composable
fun LoadingDialog(
    @StringRes text: Int? = null,
    onDismissRequest: (() -> Unit)? = null
) {
    return Dialog(
        onDismissRequest = { onDismissRequest?.invoke() },
        DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .background(
                    Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(4.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(40.dp)
                )
                if (text != null) {
                    Text(
                        text = stringResource(id = text),
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight(600),
                            color = Color.White,
                        )
                    )
                }
            }
        }
    }
}


@Composable
@Preview
fun LoadingDialogPreview() {
    MVITheme {
        Surface {
            LoadingDialog()
        }
    }
}

@Composable
@Preview
fun LoadingDialogWithTextPreview() {
    MVITheme {
        Surface {
            LoadingDialog(R.string.loading)
        }
    }
}