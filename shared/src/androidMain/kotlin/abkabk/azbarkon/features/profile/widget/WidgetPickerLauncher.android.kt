package abkabk.azbarkon.features.profile.widget

import abkabk.azbarkon.core.widget.RandomDistichWidgetProvider
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberWidgetPickerLauncher(): (() -> Unit)? {
    val context = LocalContext.current
    return remember {
        {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val pinned =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appWidgetManager.requestPinAppWidget(
                        ComponentName(context, RandomDistichWidgetProvider::class.java),
                        null,
                        null,
                    )
                } else {
                    false
                }
            if (!pinned) {
                context.startActivity(Intent(AppWidgetManager.ACTION_APPWIDGET_PICK))
            }
        }
    }
}
