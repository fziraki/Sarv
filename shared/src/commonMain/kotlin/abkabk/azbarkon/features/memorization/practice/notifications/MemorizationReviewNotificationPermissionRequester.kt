package abkabk.azbarkon.features.memorization.practice.notifications

import androidx.compose.runtime.Composable

@Composable
expect fun rememberMemorizationReviewNotificationPermissionRequester(
    onResult: (Boolean) -> Unit,
): () -> Unit
