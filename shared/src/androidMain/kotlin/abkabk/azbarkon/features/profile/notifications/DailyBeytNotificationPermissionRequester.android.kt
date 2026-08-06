package abkabk.azbarkon.features.profile.notifications

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import org.koin.compose.koinInject
import abkabk.azbarkon.domain.platform.NotificationPermissionGateway

@Composable
actual fun rememberDailyBeytNotificationPermissionRequester(
    onResult: (Boolean) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val permissionGateway: NotificationPermissionGateway = koinInject()
    var shouldRequest by remember { mutableStateOf(false) }
    val currentOnResult by rememberUpdatedState(onResult)

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { granted ->
            shouldRequest = false
            currentOnResult(granted)
        }

    LaunchedEffect(shouldRequest) {
        if (!shouldRequest) return@LaunchedEffect

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            shouldRequest = false
            val enabled = permissionGateway.areNotificationsEnabled()
            currentOnResult(enabled)
            return@LaunchedEffect
        }

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            shouldRequest = false
            currentOnResult(true)
        } else {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    return remember {
        {
            shouldRequest = true
        }
    }
}
