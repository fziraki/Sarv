package abkabk.azbarkon.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import abkabk.azbarkon.shared.R

internal object DailyBeytNotificationChannels {
    fun ensureCreated(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel =
            NotificationChannel(
                DailyBeytNotificationPayload.CHANNEL_ID,
                context.getString(R.string.daily_beyt_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = context.getString(R.string.daily_beyt_notification_channel_description)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
