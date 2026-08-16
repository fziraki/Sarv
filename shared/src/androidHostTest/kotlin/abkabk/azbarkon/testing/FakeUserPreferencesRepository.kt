package abkabk.azbarkon.testing

import abkabk.azbarkon.domain.model.ThemeMode
import abkabk.azbarkon.domain.model.profile.GameProfileStats
import abkabk.azbarkon.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeUserPreferencesRepository : UserPreferencesRepository {
    private var dailyBeytEnabled: Boolean = false
    private var memorizationReminderEnabled: Boolean = true
    private var notificationPermissionDeclineCount: Int = 0
    private var themeMode: ThemeMode = ThemeMode.System
    private var coinBalance: Int = 700
    private var visitStreak: Int = 0
    private var totalCorrect: Int = 0
    private var totalWrong: Int = 0
    private var lastPlayDayKey: Int? = null
    private var completedSessions: Int = 0
    private var perfectGameSessions: Int = 0

    private val gameStatsState =
        MutableStateFlow(
            GameProfileStats(
                visitStreak = visitStreak,
                totalCorrectAnswers = totalCorrect,
                totalWrongAnswers = totalWrong,
                coinBalance = coinBalance,
                completedSessions = completedSessions,
                perfectGameSessions = perfectGameSessions,
            ),
        )
    private val themeModeState = MutableStateFlow(themeMode)

    override fun isDailyBeytNotificationEnabled(): Boolean = dailyBeytEnabled

    override fun setDailyBeytNotificationEnabled(enabled: Boolean) {
        dailyBeytEnabled = enabled
    }

    override fun isMemorizationReminderEnabled(): Boolean = memorizationReminderEnabled

    override fun setMemorizationReminderEnabled(enabled: Boolean) {
        memorizationReminderEnabled = enabled
    }

    override fun getNotificationPermissionDeclineCount(): Int = notificationPermissionDeclineCount

    override fun incrementNotificationPermissionDeclineCount() {
        notificationPermissionDeclineCount += 1
    }

    override fun getThemeMode(): ThemeMode = themeMode

    override fun setThemeMode(mode: ThemeMode) {
        themeMode = mode
        themeModeState.value = mode
    }

    override fun observeThemeMode(): Flow<ThemeMode> = themeModeState

    override suspend fun getCoinBalance(): Int = coinBalance

    override fun adjustCoinBalance(delta: Int): Int {
        coinBalance = (coinBalance + delta).coerceAtLeast(0)
        emitGameStats()
        return coinBalance
    }

    override fun observeGameStats(): Flow<GameProfileStats> = gameStatsState

    override fun recordCompletedSession(
        correct: Int,
        wrong: Int,
        playedAtMillis: Long,
        isPerfect: Boolean,
    ) {
        val playDayKey = (playedAtMillis / 86_400_000L).toInt()
        visitStreak =
            when (lastPlayDayKey) {
                null -> 1
                playDayKey -> visitStreak
                playDayKey - 1 -> visitStreak + 1
                else -> 1
            }
        lastPlayDayKey = playDayKey
        totalCorrect += correct
        totalWrong += wrong
        completedSessions += 1
        if (isPerfect) {
            perfectGameSessions += 1
        }
        emitGameStats()
    }

    private fun emitGameStats() {
        gameStatsState.value =
            GameProfileStats(
                visitStreak = visitStreak,
                totalCorrectAnswers = totalCorrect,
                totalWrongAnswers = totalWrong,
                coinBalance = coinBalance,
                completedSessions = completedSessions,
                perfectGameSessions = perfectGameSessions,
            )
    }
}
