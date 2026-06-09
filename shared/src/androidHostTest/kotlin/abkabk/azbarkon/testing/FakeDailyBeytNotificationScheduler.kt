package abkabk.azbarkon.testing

import abkabk.azbarkon.domain.platform.DailyBeytNotificationScheduler

class FakeDailyBeytNotificationScheduler : DailyBeytNotificationScheduler {
    var isEnabled: Boolean = false
    var enableCallCount: Int = 0
    var disableCallCount: Int = 0
    var rescheduleCallCount: Int = 0
    var lastDeliveryHour: Int? = null
    var lastDeliveryMinute: Int? = null
    var lastShowImmediately: Boolean? = null

    override fun enable(
        deliveryHour: Int,
        deliveryMinute: Int,
        showImmediately: Boolean,
    ) {
        isEnabled = true
        enableCallCount++
        lastDeliveryHour = deliveryHour
        lastDeliveryMinute = deliveryMinute
        lastShowImmediately = showImmediately
    }

    override fun disable() {
        isEnabled = false
        disableCallCount++
    }

    override fun rescheduleIfEnabled() {
        rescheduleCallCount++
    }
}
