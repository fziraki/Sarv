package abkabk.azbarkon.core.notifications

import abkabk.azbarkon.domain.repository.DailyBeytRepository
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DailyBeytWorker(
    context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters),
    KoinComponent {
    private val dailyBeytRepository: DailyBeytRepository by inject()
    private val notificationPresenter: DailyBeytNotificationPresenter by inject()

    override suspend fun doWork(): androidx.work.ListenableWorker.Result {
        return when (val result = dailyBeytRepository.getTodayDistich()) {
            is abkabk.azbarkon.core.domain.result.Result.Success -> {
                notificationPresenter.show(result.data)
                androidx.work.ListenableWorker.Result.success()
            }
            is abkabk.azbarkon.core.domain.result.Result.Error -> {
                androidx.work.ListenableWorker.Result.failure()
            }
        }
    }
}
