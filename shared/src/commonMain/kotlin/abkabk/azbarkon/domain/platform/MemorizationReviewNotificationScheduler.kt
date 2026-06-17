package abkabk.azbarkon.domain.platform

interface MemorizationReviewNotificationScheduler {
    fun enable(
        deliveryHour: Int = MemorizationReviewDefaults.DELIVERY_HOUR,
        deliveryMinute: Int = MemorizationReviewDefaults.DELIVERY_MINUTE,
    )

    fun disable()

    fun rescheduleIfActive()
}

object MemorizationReviewDefaults {
    const val DELIVERY_HOUR = 10
    const val DELIVERY_MINUTE = 0
}
