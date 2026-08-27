package abkabk.azbarkon.core.platform

import platform.Foundation.NSUserDefaults

actual class KeyValueStore {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun getIntSet(key: String): Set<Int> =
        defaults
            .stringForKey(key)
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            .orEmpty()

    actual fun putIntSet(
        key: String,
        values: Set<Int>,
    ) {
        defaults.setObject(values.joinToString(","), key)
    }

    actual fun getBoolean(
        key: String,
        default: Boolean,
    ): Boolean =
        if (defaults.objectForKey(key) == null) {
            default
        } else {
            defaults.boolForKey(key)
        }

    actual fun putBoolean(
        key: String,
        value: Boolean,
    ) {
        defaults.setBool(value, key)
    }

    actual fun getInt(
        key: String,
        default: Int,
    ): Int =
        if (defaults.objectForKey(key) == null) {
            default
        } else {
            defaults.integerForKey(key).toInt()
        }

    actual fun putInt(
        key: String,
        value: Int,
    ) {
        defaults.setInteger(value.toLong(), key)
    }
}
