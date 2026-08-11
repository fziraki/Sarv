package abkabk.azbarkon.ui.components

import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AzbarkonAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    dismissButton: @Composable (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismissRequest) {
        AzbarkonAlertDialogContent(
            modifier = modifier,
            title = title,
            text = text,
            confirmButton = confirmButton,
            dismissButton = dismissButton,
        )
    }
}

@Composable
private fun AzbarkonAlertDialogContent(
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    dismissButton: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = AlertDialogDefaults.shape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = AlertDialogDefaults.TonalElevation,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            title?.invoke()
            text?.invoke()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                dismissButton?.invoke()
                confirmButton()
            }
        }
    }
}

@Preview
@Composable
private fun AzbarkonAlertDialogPreview() {
    AzbarkonTheme {
        AzbarkonAlertDialogContent(
            title = { Text("حذف همه اشعار") },
            text = { Text(
                text = "همه اشعار از لیست حذف خواهند شد.",
                style = MaterialTheme.typography.bodySmall,
            ) },
            confirmButton = {
                TextButton(onClick = {}) {
                    Text("تأیید")
                }
            },
            dismissButton = {
                TextButton(onClick = {}) {
                    Text("لغو")
                }
            },
        )
    }
}
