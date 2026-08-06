package abkabk.azbarkon.core.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RandomDistichWidgetRefresher :
    KoinComponent {
    private val updater: RandomDistichWidgetUpdater by inject()
    private val ioDispatcher: CoroutineDispatcher by inject()

    suspend fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds =
            appWidgetManager.getAppWidgetIds(
                ComponentName(context, RandomDistichWidgetProvider::class.java),
            )
        appWidgetIds.forEach { appWidgetId ->
            updater.update(
                context = context,
                appWidgetManager = appWidgetManager,
                appWidgetId = appWidgetId,
            )
        }
    }

    fun updateAllWidgetsAsync(context: Context) {
        CoroutineScope(ioDispatcher).launch {
            updateAllWidgets(context.applicationContext)
        }
    }
}
