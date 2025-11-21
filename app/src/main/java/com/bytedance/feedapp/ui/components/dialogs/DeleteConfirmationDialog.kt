package com.bytedance.feedapp.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.bytedance.feedapp.constants.StringsConstants

/**
 * 显示一个标准的删除确认对话框。
 *
 * @param onConfirm 用户点击“确认”时的回调。
 * @param onCancel 用户点击“取消”或关闭对话框时的回调。
 */
@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
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
