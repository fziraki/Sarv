package abkabk.azbarkon.core.notifications

import abkabk.azbarkon.domain.model.RandomDistich
import abkabk.azbarkon.shared.R
import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class DailyBeytNotificationPresenter(
    private val context: Context,
) {
    fun showPreview(distich: RandomDistich) {
        if (!canPostNotifications()) return

        DailyBeytNotificationChannels.ensureCreated(context)

        val notification =
            NotificationCompat
                .Builder(context, DailyBeytNotificationPayload.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.daily_beyt_notification_title))
                .setContentText(distichBody(distich))
                .setSubText(distich.poetName)
                .setContentIntent(contentIntent())
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

        NotificationManagerCompat.from(context).notify(
            DailyBeytNotificationPayload.PREVIEW_NOTIFICATION_ID,
            notification,
        )
    }

    fun show(distich: RandomDistich) {
        if (!canPostNotifications()) return

        DailyBeytNotificationChannels.ensureCreated(context)

        val expandedView = buildRemoteViews(distich, expanded = true)
        val collapsedView = buildRemoteViews(distich, expanded = false)

        val notification =
            NotificationCompat
                .Builder(context, DailyBeytNotificationPayload.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.daily_beyt_notification_title))
                .setContentText(distichBody(distich))
                .setSubText(distich.poetName)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setContentIntent(contentIntent())
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

        NotificationManagerCompat.from(context).notify(
            DailyBeytNotificationPayload.NOTIFICATION_ID,
            notification,
        )
    }

    private fun distichBody(distich: RandomDistich): String =
        "${distich.rightText}\n${distich.leftText}"

    private fun canPostNotifications(): Boolean {
        val notificationManager = NotificationManagerCompat.from(context)
        if (!notificationManager.areNotificationsEnabled()) return false

        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun contentIntent(): PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            context.packageManager.getLaunchIntentForPackage(context.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    private fun buildRemoteViews(
        distich: RandomDistich,
        expanded: Boolean,
    ): RemoteViews {
        val layoutId =
            if (expanded) {
                R.layout.notification_daily_beyt
            } else {
                R.layout.notification_daily_beyt_collapsed
            }
        return RemoteViews(context.packageName, layoutId).apply {
            setTextViewText(R.id.notification_title, context.getString(R.string.daily_beyt_notification_title))
            setTextViewText(R.id.notification_right_line, distich.rightText)
            setTextViewText(R.id.notification_left_line, distich.leftText)
            setTextViewText(R.id.notification_poet_name, distich.poetName)
        }
    }
}
