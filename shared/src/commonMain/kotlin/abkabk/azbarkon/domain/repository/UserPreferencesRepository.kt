package abkabk.azbarkon.domain.repository

interface UserPreferencesRepository {
    fun isDailyBeytNotificationEnabled(): Boolean

    fun setDailyBeytNotificationEnabled(enabled: Boolean)
}
