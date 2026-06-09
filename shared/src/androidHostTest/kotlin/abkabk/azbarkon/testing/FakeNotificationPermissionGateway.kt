package abkabk.azbarkon.testing

import abkabk.azbarkon.domain.platform.NotificationPermissionGateway

class FakeNotificationPermissionGateway(
    private var granted: Boolean = true,
) : NotificationPermissionGateway {
    var requestPermissionCallCount: Int = 0

    fun setGranted(value: Boolean) {
        granted = value
    }

    override fun areNotificationsEnabled(): Boolean = granted

    override suspend fun requestPermission(): Boolean {
        requestPermissionCallCount++
        return granted
    }
}
