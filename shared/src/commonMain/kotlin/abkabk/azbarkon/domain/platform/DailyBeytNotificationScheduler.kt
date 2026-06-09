package abkabk.azbarkon.domain.platform

interface DailyBeytNotificationScheduler {
    fun enable(
        deliveryHour: Int = 8,
        deliveryMinute: Int = 0,
        showImmediately: Boolean = false,
    )

    fun disable()

    fun rescheduleIfEnabled()
}
