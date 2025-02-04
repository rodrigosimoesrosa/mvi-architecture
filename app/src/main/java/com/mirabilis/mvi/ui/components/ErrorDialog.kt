package com.mirabilis.mvi.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mirabilis.mvi.R
import com.mirabilis.mvi.ui.theme.MVITheme

@Composable
fun ErrorDialog(
    dismissOnBackPress: Boolean = false,
    dismissOnClickOutside: Boolean = false,
    onDismissRequest: (() -> Unit)? = null,
    icon: ImageVector = Icons.Outlined.Info,
    @StringRes title: Int,
    onConfirm: () -> Unit,
    @StringRes subTitle: Int? = null,
    onCancel: (() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = { onDismissRequest?.invoke() },
        DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(vertical = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp),
                    tint = Color.Black
                )

                Text(
                    text = stringResource(title),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (subTitle != null) {
                    Text(
                        text = stringResource(subTitle),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    if (onCancel != null) {
                        OutlinedButton(
                            enabled = true,
                            onClick = { onCancel.invoke() },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.Black),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.White,
                            ),
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(40.dp)
                                .padding(start = 6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.error_dialog_cancel),
                                color = Color.Black,
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight(400),
                                    textAlign = TextAlign.Center,
                                )
                            )
                        }
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp),
                        onClick = { onConfirm.invoke() }
                    ) {
                        Text(
                            text = stringResource(R.string.error_dialog_ok)
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun SignInDialogPreview() {
    MVITheme {
        ErrorDialog(
            title = R.string.sign_in_title_error,
            icon = Icons.Outlined.Info,
            subTitle = R.string.sign_in_error_message,
            onConfirm = {}
        )
    }
}