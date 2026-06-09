package abkabk.azbarkon.core.widget

import android.content.Context
import androidx.core.content.edit

class RandomDistichWidgetPreferences(
    context: Context,
) {
    private val prefs =
        context.applicationContext.getSharedPreferences(
            RandomDistichWidgetConstants.PREFS_NAME,
            Context.MODE_PRIVATE,
        )

    fun savePoetId(
        appWidgetId: Int,
        poetId: Int,
    ) {
        prefs
            .edit {
                putInt(RandomDistichWidgetConstants.poetIdKey(appWidgetId), poetId)
            }
    }

    fun getPoetId(appWidgetId: Int): Int =
        prefs.getInt(
            RandomDistichWidgetConstants.poetIdKey(appWidgetId),
            RandomDistichWidgetConstants.ALL_POETS_ID,
        )

    fun clear(appWidgetId: Int) {
        prefs
            .edit {
                remove(RandomDistichWidgetConstants.poetIdKey(appWidgetId))
            }
    }
}
