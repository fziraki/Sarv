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

    actual fun getBoolean(
        key: String,
        default: Boolean,
    ): Boolean = preferences.getBoolean(key, default)

    actual fun putBoolean(
        key: String,
        value: Boolean,
    ) {
        preferences.edit {
            putBoolean(key, value)
        }
    }

    actual fun getInt(
        key: String,
        default: Int,
    ): Int = preferences.getInt(key, default)

    actual fun putInt(
        key: String,
        value: Int,
    ) {
        preferences.edit {
            putInt(key, value)
        }
    }

    private companion object {
        const val PREFS_NAME = "azbarkon_prefs"
    }
}
