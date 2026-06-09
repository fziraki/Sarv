package abkabk.azbarkon.data.platform

import abkabk.azbarkon.domain.platform.NotificationPermissionGateway
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

class IosNotificationPermissionGateway : NotificationPermissionGateway {
    override fun areNotificationsEnabled(): Boolean = true

    override suspend fun requestPermission(): Boolean =
        suspendCancellableCoroutine { continuation ->
            UNUserNotificationCenter.currentNotificationCenter().requestAuthorizationWithOptions(
                options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge,
                completionHandler = { granted, _ ->
                    if (continuation.isActive) {
                        continuation.resume(granted)
                    }
                },
            )
        }
}
