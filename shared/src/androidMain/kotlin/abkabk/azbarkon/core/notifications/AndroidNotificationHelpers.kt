package abkabk.azbarkon.core.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

fun Context.canPostNotifications(): Boolean {
    val notificationManager = NotificationManagerCompat.from(this)
    if (!notificationManager.areNotificationsEnabled()) return false

    return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
}

fun Context.ensureNotificationChannel(
    channelId: String,
    name: String,
    description: String? = null,
    lockscreenVisibility: Int? = null,
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val channel =
        NotificationChannel(
            channelId,
            name,
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            if (description != null) this.description = description
            if (lockscreenVisibility != null) this.lockscreenVisibility = lockscreenVisibility
        }

    val notificationManager =
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

fun Context.launchAppPendingIntent(
    requestCode: Int,
    configure: Intent.() -> Unit = {},
): PendingIntent {
    val launchIntent =
        packageManager.getLaunchIntentForPackage(packageName) ?: Intent()
    launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    launchIntent.configure()
    return PendingIntent.getActivity(
        this,
        requestCode,
        launchIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
