package abkabk.azbarkon.core.notifications

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import org.koin.core.context.GlobalContext

actual fun openAppNotificationSettings() {
    val context = GlobalContext.get().get<Context>()
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