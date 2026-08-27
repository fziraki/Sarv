package abkabk.azbarkon.core.platform

expect class KeyValueStore {
    fun getIntSet(key: String): Set<Int>

    fun putIntSet(
        key: String,
        values: Set<Int>,
    )

    fun getBoolean(
        key: String,
        default: Boolean = false,
    ): Boolean

    fun putBoolean(
        key: String,
        value: Boolean,
    )

    fun getInt(
        key: String,
        default: Int = 0,
    ): Int

    fun putInt(
        key: String,
        value: Int,
    )
}
