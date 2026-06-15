package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.platform.KeyValueStore
import abkabk.azbarkon.domain.repository.UserPreferencesRepository
import abkabk.azbarkon.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalUserPreferencesRepository(
    private val keyValueStore: KeyValueStore,
    private val userRepository: UserRepository,
) : UserPreferencesRepository {
    override fun isDailyBeytNotificationEnabled(): Boolean =
        keyValueStore.getBoolean(KEY_DAILY_BEYT_NOTIFICATIONS_ENABLED)

    override fun setDailyBeytNotificationEnabled(enabled: Boolean) {
        keyValueStore.putBoolean(KEY_DAILY_BEYT_NOTIFICATIONS_ENABLED, enabled)
    }

    override suspend fun getCoinBalance(): Int {
        if (!keyValueStore.getBoolean(KEY_COIN_INITIALIZED, default = false)) {
            withContext(Dispatchers.Default) {
                val seedBalance =
                    when (val result = userRepository.getUserInfo()) {
                        is Result.Success -> result.data.currentScore ?: 0
                        is Result.Error -> 0
                    }
                keyValueStore.putInt(KEY_COIN_BALANCE, seedBalance)
                keyValueStore.putBoolean(KEY_COIN_INITIALIZED, true)
            }
        }
        return keyValueStore.getInt(KEY_COIN_BALANCE, default = 0)
    }

    override fun adjustCoinBalance(delta: Int): Int {
        val updated = (keyValueStore.getInt(KEY_COIN_BALANCE, default = 0) + delta).coerceAtLeast(0)
        keyValueStore.putInt(KEY_COIN_BALANCE, updated)
        keyValueStore.putBoolean(KEY_COIN_INITIALIZED, true)
        return updated
    }

    internal companion object {
        const val KEY_DAILY_BEYT_NOTIFICATIONS_ENABLED = "daily_beyt_notifications_enabled"
        const val KEY_COIN_BALANCE = "game_coin_balance"
        const val KEY_COIN_INITIALIZED = "game_coin_initialized"
    }
}
