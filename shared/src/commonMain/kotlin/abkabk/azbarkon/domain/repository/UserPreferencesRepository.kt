package abkabk.azbarkon.domain.repository

interface UserPreferencesRepository {
    fun isDailyBeytNotificationEnabled(): Boolean

    fun setDailyBeytNotificationEnabled(enabled: Boolean)

    suspend fun getCoinBalance(): Int

    fun adjustCoinBalance(delta: Int): Int
}
