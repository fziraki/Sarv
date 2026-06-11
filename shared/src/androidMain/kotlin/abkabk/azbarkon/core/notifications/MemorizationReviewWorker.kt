package abkabk.azbarkon.core.notifications

import abkabk.azbarkon.domain.datasource.MemorizationLocalDataSource
import abkabk.azbarkon.domain.platform.MemorizationReviewNotificationScheduler
import abkabk.azbarkon.core.util.currentTimeMillis
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MemorizationReviewWorker(
    context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters),
    KoinComponent {
    private val localDataSource: MemorizationLocalDataSource by inject()
    private val scheduler: MemorizationReviewNotificationScheduler by inject()
    private val notificationPresenter: MemorizationReviewNotificationPresenter by inject()

    override suspend fun doWork(): Result {
        return try {
            if (localDataSource.countActivePoems() == 0) {
                scheduler.disable()
                return Result.success()
            }

            val dueCount = localDataSource.countDueCards(currentTimeMillis())
            if (dueCount > 0) {
                notificationPresenter.show(dueCount)
            }
            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }
}
