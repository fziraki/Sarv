package abkabk.azbarkon.data.platform

import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.notifications.DailyBeytNotificationPresenter
import abkabk.azbarkon.core.notifications.DailyBeytWorkScheduler
import abkabk.azbarkon.domain.platform.DailyBeytNotificationScheduler
import abkabk.azbarkon.domain.repository.DailyBeytRepository
import abkabk.azbarkon.domain.repository.UserPreferencesRepository
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AndroidDailyBeytNotificationScheduler(
    private val context: Context,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val dailyBeytRepository: DailyBeytRepository,
    private val notificationPresenter: DailyBeytNotificationPresenter,
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
        DailyBeytWorkScheduler.schedule(
            context = context,
            deliveryHour = deliveryHour,
            deliveryMinute = deliveryMinute,
        )

        if (showImmediately) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    dailyBeytRepository.getTodayDistich()
                }.onSuccess { distich ->
                    notificationPresenter.showPreview(distich)
                }
            }
        }
    }

    override fun disable() {
        DailyBeytWorkScheduler.cancel(context)
    }

    override fun rescheduleIfEnabled() {
        if (userPreferencesRepository.isDailyBeytNotificationEnabled()) {
            enable(deliveryHour, deliveryMinute)
        }
    }

    private companion object {
        const val DEFAULT_HOUR = 8
        const val DEFAULT_MINUTE = 0
    }
}
