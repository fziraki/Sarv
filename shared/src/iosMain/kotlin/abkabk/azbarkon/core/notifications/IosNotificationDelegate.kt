package abkabk.azbarkon.core.notifications

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

object IosNotificationDelegate : NSObject(), UNUserNotificationCenterDelegateProtocol {
    val poemId = MutableStateFlow<Int?>(null)
    val openMemorizationPractice = MutableStateFlow(false)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun install() {
        UNUserNotificationCenter.currentNotificationCenter().delegate = this
    }

    override fun userNotificationCenterWillPresentNotification(
        center: UNUserNotificationCenter,
        notification: UNNotification,
        completionHandler: (ULong) -> Unit,
    ) {
        completionHandler(UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge)
    }

    override fun userNotificationCenterDidReceiveNotificationResponse(
        center: UNUserNotificationCenter,
        response: UNNotificationResponse,
        completionHandler: () -> Unit,
    ) {
        val userInfo = response.notification.request.content.userInfo
        userInfo[DailyBeytNotificationPayload.KEY_POEM_ID]
            ?.toString()
            ?.toIntOrNull()
            ?.let { poemId.value = it }

        if (userInfo[MemorizationReviewNotificationPayload.KEY_OPEN_MEMORIZATION_PRACTICE]?.toString() == "true") {
            openMemorizationPractice.value = true
            // ponytail: reset so a second tap of the same notification navigates again
            scope.launch {
                delay(1_500)
                openMemorizationPractice.value = false
            }
        }
        completionHandler()
    }
}
