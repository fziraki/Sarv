package abkabk.azbarkon.core.widget

import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.notifications.DailyBeytNotificationPayload
import abkabk.azbarkon.domain.model.RandomDistich
import abkabk.azbarkon.domain.repository.DailyBeytRepository
import abkabk.azbarkon.shared.R
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RandomDistichWidgetUpdater :
    KoinComponent {
    private val dailyBeytRepository: DailyBeytRepository by inject()
    private val preferences: RandomDistichWidgetPreferences by inject()

    suspend fun update(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        source: WidgetDistichSource = WidgetDistichSource.Daily,
    ) {
        val poetId = preferences.getPoetId(appWidgetId)
        val seed =
            when (source) {
                WidgetDistichSource.Daily -> dailyDistichSeed(poetId)
                WidgetDistichSource.Random -> randomDistichSeed(appWidgetId)
            }
        val views = buildRemoteViews(context, appWidgetId, distich = null)

        when (val result = dailyBeytRepository.getRandomDistich(seed = seed, poetId = poetId)) {
            is Result.Success -> {
                val successViews =
                    buildRemoteViews(
                        context = context,
                        appWidgetId = appWidgetId,
                        distich = result.data,
                    )
                appWidgetManager.updateAppWidget(appWidgetId, successViews)
            }
            is Result.Error -> {
                views.setTextViewText(R.id.widget_right_line, context.getString(R.string.widget_random_distich_error))
                views.setTextViewText(R.id.widget_left_line, "")
                views.setTextViewText(R.id.widget_poet_name, "")
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    private fun buildRemoteViews(
        context: Context,
        appWidgetId: Int,
        distich: RandomDistich?,
    ): RemoteViews =
        RemoteViews(context.packageName, R.layout.widget_random_distich).apply {
            setTextViewText(R.id.widget_title, context.getString(R.string.widget_random_distich_title))

            if (distich == null) {
                setTextViewText(R.id.widget_right_line, context.getString(R.string.widget_random_distich_loading))
                setTextViewText(R.id.widget_left_line, "")
                setTextViewText(R.id.widget_poet_name, "")
            } else {
                setTextViewText(R.id.widget_right_line, distich.rightText)
                setTextViewText(R.id.widget_left_line, distich.leftText)
                setTextViewText(R.id.widget_poet_name, distich.poetName)
            }

            setOnClickPendingIntent(
                R.id.widget_refresh,
                refreshPendingIntent(context, appWidgetId),
            )

            if (distich != null) {
                setOnClickPendingIntent(
                    R.id.widget_content,
                    openPoemPendingIntent(context, distich.poemId),
                )
                setOnClickPendingIntent(
                    R.id.widget_poet_name,
                    openPoemPendingIntent(context, distich.poemId),
                )
            }
        }

    private fun refreshPendingIntent(
        context: Context,
        appWidgetId: Int,
    ): PendingIntent {
        val intent =
            Intent(context, RandomDistichWidgetProvider::class.java).apply {
                action = RandomDistichWidgetConstants.ACTION_REFRESH
                putExtra(RandomDistichWidgetConstants.EXTRA_APP_WIDGET_ID, appWidgetId)
            }
        return PendingIntent.getBroadcast(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun openPoemPendingIntent(
        context: Context,
        poemId: Int,
    ): PendingIntent {
        val intent =
            Intent().apply {
                setClassName(context.packageName, RandomDistichWidgetConstants.MAIN_ACTIVITY_CLASS)
                putExtra(DailyBeytNotificationPayload.KEY_POEM_ID, poemId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        return PendingIntent.getActivity(
            context,
            poemId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
