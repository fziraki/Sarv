package abkabk.azbarkon.domain.repository

import abkabk.azbarkon.domain.model.ThemeMode
import abkabk.azbarkon.domain.model.profile.GameProfileStats
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun isDailyBeytNotificationEnabled(): Boolean

    fun setDailyBeytNotificationEnabled(enabled: Boolean)

    fun isMemorizationReminderEnabled(): Boolean

    fun setMemorizationReminderEnabled(enabled: Boolean)

    fun getThemeMode(): ThemeMode

    fun setThemeMode(mode: ThemeMode)

    fun observeThemeMode(): Flow<ThemeMode>

    suspend fun getCoinBalance(): Int

    fun adjustCoinBalance(delta: Int): Int

    fun observeGameStats(): Flow<GameProfileStats>

    fun recordCompletedSession(
        correct: Int,
        wrong: Int,
        playedAtMillis: Long,
        isPerfect: Boolean = false,
    )

    fun getAvatarIndex(): Int

    fun setAvatarIndex(index: Int)

    fun observeAvatarIndex(): Flow<Int>
}
