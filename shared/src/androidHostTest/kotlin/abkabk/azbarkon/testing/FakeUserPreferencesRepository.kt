package abkabk.azbarkon.testing

import abkabk.azbarkon.domain.repository.UserPreferencesRepository

class FakeUserPreferencesRepository : UserPreferencesRepository {
    private var dailyBeytEnabled: Boolean = false
    private var coinBalance: Int = 700

    override fun isDailyBeytNotificationEnabled(): Boolean = dailyBeytEnabled

    override fun setDailyBeytNotificationEnabled(enabled: Boolean) {
        dailyBeytEnabled = enabled
    }

    override suspend fun getCoinBalance(): Int = coinBalance

    override fun adjustCoinBalance(delta: Int): Int {
        coinBalance = (coinBalance + delta).coerceAtLeast(0)
        return coinBalance
    }
}
