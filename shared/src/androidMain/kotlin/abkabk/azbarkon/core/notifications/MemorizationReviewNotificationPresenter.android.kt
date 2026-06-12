package abkabk.azbarkon.core.notifications

import abkabk.azbarkon.shared.R
import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class MemorizationReviewNotificationPresenter(
    private val context: Context,
) {
    fun show(dueCardCount: Int) {
        if (!canPostNotifications()) return

        MemorizationReviewNotificationChannels.ensureCreated(context)

        val launchIntent = createLaunchIntent(context)
        val contentPendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val startReviewIntent =
            PendingIntent.getActivity(
                context,
                1,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

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

    private fun canPostNotifications(): Boolean {
        val notificationManager = NotificationManagerCompat.from(context)
        if (!notificationManager.areNotificationsEnabled()) return false

        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createLaunchIntent(context: Context): Intent {
        val launchIntent =
            context.packageManager.getLaunchIntentForPackage(context.packageName)
                ?: Intent()
        launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        launchIntent.putExtra(
            MemorizationReviewNotificationPayload.KEY_OPEN_MEMORIZATION_PRACTICE,
            true,
        )
        return launchIntent
    }
}
