@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package abkabk.azbarkon.core.platform

expect class KeyValueStore {
    fun getIntSet(key: String): Set<Int>

    fun putIntSet(
        key: String,
        values: Set<Int>,
    )
}
