package abkabk.azbarkon.features.profile.notifications

import androidx.compose.runtime.Composable

@Composable
expect fun rememberDailyBeytNotificationPermissionRequester(
    onResult: (Boolean) -> Unit,
): () -> Unit
