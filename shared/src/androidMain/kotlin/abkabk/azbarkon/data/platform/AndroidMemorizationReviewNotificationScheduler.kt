package abkabk.azbarkon.data.platform

import abkabk.azbarkon.core.notifications.MemorizationReviewWorkScheduler
import abkabk.azbarkon.domain.datasource.MemorizationLocalDataSource
import abkabk.azbarkon.domain.platform.MemorizationReviewNotificationScheduler
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AndroidMemorizationReviewNotificationScheduler(
    private val context: Context,
    private val localDataSource: MemorizationLocalDataSource,
) : MemorizationReviewNotificationScheduler {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var deliveryHour: Int = DEFAULT_HOUR
    private var deliveryMinute: Int = DEFAULT_MINUTE

    override fun enable(
        deliveryHour: Int,
        deliveryMinute: Int,
    ) {
        this.deliveryHour = deliveryHour
        this.deliveryMinute = deliveryMinute
        MemorizationReviewWorkScheduler.schedule(
            context = context,
            deliveryHour = deliveryHour,
            deliveryMinute = deliveryMinute,
        )
    }

    override fun disable() {
        MemorizationReviewWorkScheduler.cancel(context)
    }

    override fun rescheduleIfActive() {
        scope.launch {
            if (localDataSource.countActivePoems() == 0) {
                disable()
            } else {
                enable(deliveryHour, deliveryMinute)
            }
        }
    }

    private companion object {
        const val DEFAULT_HOUR = 10
        const val DEFAULT_MINUTE = 0
    }
}
