package abkabk.azbarkon.testing

import abkabk.azbarkon.domain.platform.MemorizationReviewNotificationScheduler

class FakeMemorizationReviewNotificationScheduler : MemorizationReviewNotificationScheduler {
    var isEnabled: Boolean = false
    var enableCallCount: Int = 0
    var disableCallCount: Int = 0
    var rescheduleCallCount: Int = 0
    var lastDeliveryHour: Int? = null
    var lastDeliveryMinute: Int? = null

    override fun enable(
        deliveryHour: Int,
        deliveryMinute: Int,
    ) {
        isEnabled = true
        enableCallCount++
        lastDeliveryHour = deliveryHour
        lastDeliveryMinute = deliveryMinute
    }

    override fun disable() {
        isEnabled = false
        disableCallCount++
    }

    override fun rescheduleIfActive() {
        rescheduleCallCount++
    }
}
