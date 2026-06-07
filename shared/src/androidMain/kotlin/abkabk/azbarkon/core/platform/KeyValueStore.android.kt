@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package abkabk.azbarkon.core.platform

import android.content.Context
import androidx.core.content.edit

actual class KeyValueStore(
    context: Context,
) {
    private val preferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    actual fun getIntSet(key: String): Set<Int> =
        preferences
            .getStringSet(key, emptySet())
            .orEmpty()
            .mapNotNull { it.toIntOrNull() }
            .toSet()

    actual fun putIntSet(
        key: String,
        values: Set<Int>,
    ) {
        preferences
            .edit {
                putStringSet(key, values.map { it.toString() }.toSet())
            }
    }

    private companion object {
        const val PREFS_NAME = "azbarkon_prefs"
    }
}
