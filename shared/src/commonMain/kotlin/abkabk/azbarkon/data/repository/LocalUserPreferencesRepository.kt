package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.platform.KeyValueStore
import abkabk.azbarkon.domain.repository.UserPreferencesRepository

class LocalUserPreferencesRepository(
    private val keyValueStore: KeyValueStore,
) : UserPreferencesRepository {
    override fun isDailyBeytNotificationEnabled(): Boolean =
        keyValueStore.getBoolean(KEY_DAILY_BEYT_NOTIFICATIONS_ENABLED)

    override fun setDailyBeytNotificationEnabled(enabled: Boolean) {
        keyValueStore.putBoolean(KEY_DAILY_BEYT_NOTIFICATIONS_ENABLED, enabled)
    }

    internal companion object {
        const val KEY_DAILY_BEYT_NOTIFICATIONS_ENABLED = "daily_beyt_notifications_enabled"
    }
}
