package abkabk.azbarkon.domain.platform

interface NotificationPermissionGateway {
    fun areNotificationsEnabled(): Boolean

    suspend fun requestPermission(): Boolean
}
