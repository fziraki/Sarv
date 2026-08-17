package abkabk.azbarkon.core.notifications

import abkabk.azbarkon.shared.R
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MemorizationReviewNotificationPresenter(
    private val context: Context,
) {
    fun show(dueCardCount: Int) {
        if (!context.canPostNotifications()) return

        context.ensureNotificationChannel(
            MemorizationReviewNotificationPayload.CHANNEL_ID,
            context.getString(R.string.memorization_review_notification_channel_name),
            context.getString(R.string.memorization_review_notification_channel_description),
        )

        val contentPendingIntent =
            context.launchAppPendingIntent(0) {
                putExtra(MemorizationReviewNotificationPayload.KEY_OPEN_MEMORIZATION_PRACTICE, true)
            }

        val startReviewIntent =
            context.launchAppPendingIntent(1) {
                putExtra(MemorizationReviewNotificationPayload.KEY_OPEN_MEMORIZATION_PRACTICE, true)
            }

        val notification =
            NotificationCompat
                .Builder(context, MemorizationReviewNotificationPayload.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.memorization_review_notification_title))
                .setContentText(
                    context.getString(
                        R.string.memorization_review_notification_body,
                        dueCardCount,
                    ),
                )
                .setContentIntent(contentPendingIntent)
                .addAction(
                    R.drawable.ic_notification,
                    context.getString(R.string.memorization_review_notification_action),
                    startReviewIntent,
                )
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

        NotificationManagerCompat.from(context).notify(
            MemorizationReviewNotificationPayload.NOTIFICATION_ID,
            notification,
        )
    }
}
