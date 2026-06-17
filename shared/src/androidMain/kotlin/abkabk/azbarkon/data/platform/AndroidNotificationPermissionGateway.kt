package abkabk.azbarkon.data.platform

import abkabk.azbarkon.domain.platform.NotificationPermissionGateway
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class AndroidNotificationPermissionGateway(
    private val context: Context,
) : NotificationPermissionGateway {
    override fun areNotificationsEnabled(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled() &&
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS,
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    override suspend fun requestPermission(): Boolean = areNotificationsEnabled()
}
