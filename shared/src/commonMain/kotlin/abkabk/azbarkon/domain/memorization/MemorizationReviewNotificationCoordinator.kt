package abkabk.azbarkon.domain.memorization

import abkabk.azbarkon.domain.datasource.MemorizationLocalDataSource
import abkabk.azbarkon.domain.platform.MemorizationReviewDefaults
import abkabk.azbarkon.domain.platform.MemorizationReviewNotificationScheduler
import abkabk.azbarkon.domain.repository.UserPreferencesRepository

class MemorizationReviewNotificationCoordinator(
    private val localDataSource: MemorizationLocalDataSource,
    private val scheduler: MemorizationReviewNotificationScheduler,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend fun sync() {
        if (
            !userPreferencesRepository.isMemorizationReminderEnabled() ||
            localDataSource.countActivePoems() == 0
        ) {
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
