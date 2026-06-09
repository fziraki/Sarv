package abkabk.azbarkon.testing

import abkabk.azbarkon.domain.repository.UserPreferencesRepository

class FakeUserPreferencesRepository : UserPreferencesRepository {
    private var dailyBeytEnabled: Boolean = false

    override fun isDailyBeytNotificationEnabled(): Boolean = dailyBeytEnabled

    override fun setDailyBeytNotificationEnabled(enabled: Boolean) {
        dailyBeytEnabled = enabled
    }
}
