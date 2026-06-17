package abkabk.azbarkon

import abkabk.azbarkon.core.di.initKoin
import abkabk.azbarkon.domain.platform.DailyBeytNotificationScheduler
import abkabk.azbarkon.domain.platform.MemorizationReviewNotificationScheduler
import android.app.Application
import androidx.work.Configuration
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.android.inject
import org.koin.androidx.workmanager.factory.KoinWorkerFactory

class AzbarkonApp :
    Application(),
    Configuration.Provider {
    private val dailyBeytNotificationScheduler: DailyBeytNotificationScheduler by inject()
    private val memorizationReviewNotificationScheduler: MemorizationReviewNotificationScheduler by inject()

    override fun onCreate() {
        super.onCreate()

        initKoin(this)
        Napier.base(DebugAntilog())
        dailyBeytNotificationScheduler.rescheduleIfEnabled()
        memorizationReviewNotificationScheduler.rescheduleIfActive()
    }

    override val workManagerConfiguration: Configuration
        get() =
            Configuration
                .Builder()
                .setWorkerFactory(KoinWorkerFactory())
                .build()
}
