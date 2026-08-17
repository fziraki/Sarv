package abkabk.azbarkon.core.notifications

import abkabk.azbarkon.domain.platform.NotificationPermissionGateway
import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
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

@Composable
actual fun rememberNotificationPermissionRequester(
    onResult: (Boolean) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val permissionGateway: NotificationPermissionGateway = koinInject()
    var shouldRequest by remember { mutableStateOf(false) }
    var hasRequestedBefore by remember { mutableStateOf(false) }
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
            if (permissionGateway.areNotificationsEnabled()) {
                currentOnResult(true)
            } else {
                currentOnResult(false)
                openNotificationSettings(context)
            }
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
        } else if (
            !hasRequestedBefore ||
            activity?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) == true
        ) {
            hasRequestedBefore = true
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            shouldRequest = false
            currentOnResult(false)
            openNotificationSettings(context)
        }
    }

    return remember {
        {
            shouldRequest = true
        }
    }
}

private fun openNotificationSettings(context: Context) {
    val intent =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else {
            Intent(Settings.ACTION_SETTINGS)
        }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
