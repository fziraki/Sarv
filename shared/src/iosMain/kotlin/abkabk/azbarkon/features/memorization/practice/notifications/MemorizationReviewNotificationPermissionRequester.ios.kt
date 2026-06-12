package abkabk.azbarkon.features.memorization.practice.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import abkabk.azbarkon.domain.platform.NotificationPermissionGateway

@Composable
actual fun rememberMemorizationReviewNotificationPermissionRequester(
    onResult: (Boolean) -> Unit,
): () -> Unit {
    val permissionGateway: NotificationPermissionGateway = koinInject()
    val scope = rememberCoroutineScope()
    var shouldRequest by remember { mutableStateOf(false) }

    if (shouldRequest) {
        shouldRequest = false
        scope.launch {
            onResult(permissionGateway.requestPermission())
        }
    }

    return remember {
        {
            shouldRequest = true
        }
    }
}
