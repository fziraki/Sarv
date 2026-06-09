package abkabk.azbarkon.data.platform

import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.notifications.DailyBeytNotificationPayload
import abkabk.azbarkon.domain.platform.DailyBeytNotificationScheduler
import abkabk.azbarkon.domain.repository.DailyBeytRepository
import abkabk.azbarkon.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationAction
import platform.UserNotifications.UNNotificationCategory
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter

class IosDailyBeytNotificationScheduler(
    private val dailyBeytRepository: DailyBeytRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : DailyBeytNotificationScheduler {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var deliveryHour: Int = DEFAULT_HOUR
    private var deliveryMinute: Int = DEFAULT_MINUTE

    override fun enable(
        deliveryHour: Int,
        deliveryMinute: Int,
        showImmediately: Boolean,
    ) {
        this.deliveryHour = deliveryHour
        this.deliveryMinute = deliveryMinute
        scope.launch {
            scheduleNotification(showImmediately = showImmediately)
        }
    }

    override fun disable() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removePendingNotificationRequestsWithIdentifiers(
            listOf(
                DailyBeytNotificationPayload.REQUEST_ID,
                DailyBeytNotificationPayload.IMMEDIATE_REQUEST_ID,
            ),
        )
        center.removeDeliveredNotificationsWithIdentifiers(
            listOf(
                DailyBeytNotificationPayload.REQUEST_ID,
                DailyBeytNotificationPayload.IMMEDIATE_REQUEST_ID,
            ),
        )
    }

    override fun rescheduleIfEnabled() {
        if (userPreferencesRepository.isDailyBeytNotificationEnabled()) {
            enable(deliveryHour, deliveryMinute)
        }
    }

    private suspend fun scheduleNotification(showImmediately: Boolean) {
        registerCategory()
        val center = UNUserNotificationCenter.currentNotificationCenter()

        dailyBeytRepository.getTodayDistich().onSuccess { distich ->
            if (showImmediately) {
                val immediateRequest =
                    UNNotificationRequest.requestWithIdentifier(
                        identifier = DailyBeytNotificationPayload.IMMEDIATE_REQUEST_ID,
                        content = buildPreviewContent(distich),
                        trigger = null,
                    )
                center.addNotificationRequest(immediateRequest, withCompletionHandler = null)
            }

            val content = buildNotificationContent(distich)

            val dateComponents =
                NSDateComponents().apply {
                    hour = deliveryHour.toLong()
                    minute = deliveryMinute.toLong()
                }

            val trigger =
                UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                    dateComponents = dateComponents,
                    repeats = true,
                )

            val request =
                UNNotificationRequest.requestWithIdentifier(
                    identifier = DailyBeytNotificationPayload.REQUEST_ID,
                    content = content,
                    trigger = trigger,
                )

            center.removePendingNotificationRequestsWithIdentifiers(
                listOf(DailyBeytNotificationPayload.REQUEST_ID),
            )
            center.addNotificationRequest(request, withCompletionHandler = null)
        }
    }

    private fun buildPreviewContent(distich: abkabk.azbarkon.domain.model.RandomDistich): UNMutableNotificationContent =
        UNMutableNotificationContent().apply {
            setTitle("بیت امروز")
            setSubtitle(distich.poetName)
            setBody("${distich.rightText}\n${distich.leftText}")
        }

    private fun buildNotificationContent(distich: abkabk.azbarkon.domain.model.RandomDistich): UNMutableNotificationContent =
        UNMutableNotificationContent().apply {
            setTitle("بیت امروز")
            setSubtitle(distich.poetName)
            setBody("${distich.rightText}\n${distich.leftText}")
            setCategoryIdentifier(DAILY_BEYT_CATEGORY)
            setUserInfo(
                mapOf<Any?, Any?>(
                    DailyBeytNotificationPayload.KEY_POET_NAME to distich.poetName,
                    DailyBeytNotificationPayload.KEY_RIGHT_TEXT to distich.rightText,
                    DailyBeytNotificationPayload.KEY_LEFT_TEXT to distich.leftText,
                    DailyBeytNotificationPayload.KEY_POEM_ID to distich.poemId.toString(),
                    DailyBeytNotificationPayload.KEY_VORDER to distich.vorder.toString(),
                ),
            )
        }

    private fun registerCategory() {
        val category =
            UNNotificationCategory.categoryWithIdentifier(
                identifier = DAILY_BEYT_CATEGORY,
                actions = emptyList<UNNotificationAction>(),
                intentIdentifiers = emptyList<String>(),
                options = 0uL,
            )
        UNUserNotificationCenter.currentNotificationCenter().setNotificationCategories(setOf(category))
    }

    private companion object {
        const val DEFAULT_HOUR = 8
        const val DEFAULT_MINUTE = 0
        const val DAILY_BEYT_CATEGORY = "DAILY_BEYT"
    }
}
