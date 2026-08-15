package abkabk.azbarkon.ui.components

import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AzbarkonAlertDialog(
    onDismissRequest: () -> Unit,
    confirmLabel: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    text: String? = null,
    dismissLabel: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmLabel) } },
        modifier = modifier,
        title = title?.let { { Text(it) } },
        text = text?.let { { Text(it) } },
        dismissButton = dismissLabel?.let {
            { TextButton(onClick = onDismissRequest) { Text(it) } }
        },
        containerColor = containerColor,
    )
}

@Preview
@Composable
private fun AzbarkonAlertDialogPreview() {
    AzbarkonTheme {
        AzbarkonAlertDialog(
            title = "حذف همه اشعار",
            text = "همه اشعار از لیست حذف خواهند شد.",
            confirmLabel = "تأیید",
            onConfirm = {},
            dismissLabel = "لغو",
            onDismissRequest = {},
        )
    }
}
