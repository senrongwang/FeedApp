package com.bytedance.feedapp.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.bytedance.feedapp.constants.StringsConstants

/**
 * `DeleteConfirmationDialog` 是一个 Composable 函数，用于显示一个确认删除操作的对话框。
 *
 * @param onConfirm 用户点击“确认”按钮时调用的回调函数。
 * @param onCancel 用户点击“取消”按钮时调用的回调函数。
 */
@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel, // 当用户点击对话框外部或按下返回键时，也视为取消。
        title = {
            Text(text = StringsConstants.DELETE_CONFIRMATION_TITLE)
        },
        text = {
            Text(text = StringsConstants.DELETE_CONFIRMATION_MESSAGE)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(StringsConstants.CONFIRM)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(StringsConstants.CANCEL)
            }
        }
    )
}