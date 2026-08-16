package abkabk.azbarkon.core.notifications

import androidx.compose.runtime.Composable

@Composable
expect fun rememberNotificationPermissionRequester(
    onResult: (Boolean) -> Unit,
): () -> Unit
