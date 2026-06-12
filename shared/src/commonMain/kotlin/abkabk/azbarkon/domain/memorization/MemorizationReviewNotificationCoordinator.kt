package abkabk.azbarkon.domain.memorization

import abkabk.azbarkon.domain.datasource.MemorizationLocalDataSource
import abkabk.azbarkon.domain.platform.MemorizationReviewDefaults
import abkabk.azbarkon.domain.platform.MemorizationReviewNotificationScheduler

class MemorizationReviewNotificationCoordinator(
    private val localDataSource: MemorizationLocalDataSource,
    private val scheduler: MemorizationReviewNotificationScheduler,
) {
    suspend fun sync() {
        if (localDataSource.countActivePoems() == 0) {
            scheduler.disable()
        } else {
            scheduler.enable(
                deliveryHour = MemorizationReviewDefaults.DELIVERY_HOUR,
                deliveryMinute = MemorizationReviewDefaults.DELIVERY_MINUTE,
            )
        }
    }

    suspend fun rescheduleIfActive() {
        sync()
    }
}
