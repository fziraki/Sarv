package abkabk.azbarkon.core.notifications

import abkabk.azbarkon.shared.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

internal object MemorizationReviewNotificationChannels {
    fun ensureCreated(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel =
            NotificationChannel(
                MemorizationReviewNotificationPayload.CHANNEL_ID,
                context.getString(R.string.memorization_review_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description =
                    context.getString(R.string.memorization_review_notification_channel_description)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
