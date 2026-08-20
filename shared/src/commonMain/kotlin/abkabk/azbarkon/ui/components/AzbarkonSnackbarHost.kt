package abkabk.azbarkon.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AzbarkonSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data ->
        val isSuccess = (data.visuals as? AzbarkonSnackbarVisuals)?.isSuccess == true
        val containerColor =
            if (isSuccess) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.error
            }
        val contentColor =
            if (isSuccess) {
                MaterialTheme.colorScheme.onSecondary
            } else {
                MaterialTheme.colorScheme.onError
            }
        Snackbar(
            snackbarData = data,
            containerColor = containerColor,
            contentColor = contentColor,
            actionColor = contentColor,
        )
    }
}