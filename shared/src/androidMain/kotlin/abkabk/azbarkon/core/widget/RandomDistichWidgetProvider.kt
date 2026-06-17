package abkabk.azbarkon.core.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RandomDistichWidgetProvider :
    android.appwidget.AppWidgetProvider(),
    KoinComponent {
    private val updater: RandomDistichWidgetUpdater by inject()
    private val preferences: RandomDistichWidgetPreferences by inject()
    private val refresher: RandomDistichWidgetRefresher by inject()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                appWidgetIds.forEach { appWidgetId ->
                    updater.update(
                        context = context,
                        appWidgetManager = appWidgetManager,
                        appWidgetId = appWidgetId,
                    )
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onDeleted(
        context: Context,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach(preferences::clear)
    }

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        super.onReceive(context, intent)

        when (intent.action) {
            RandomDistichWidgetConstants.ACTION_REFRESH -> {
                val appWidgetId =
                    intent.getIntExtra(
                        RandomDistichWidgetConstants.EXTRA_APP_WIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID,
                    )
                if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return

                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        updater.update(
                            context = context,
                            appWidgetManager = AppWidgetManager.getInstance(context),
                            appWidgetId = appWidgetId,
                            source = WidgetDistichSource.Random,
                        )
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        refresher.updateAllWidgets(context.applicationContext)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}
