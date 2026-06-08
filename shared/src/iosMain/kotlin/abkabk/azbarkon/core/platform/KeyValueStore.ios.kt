@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

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
}
