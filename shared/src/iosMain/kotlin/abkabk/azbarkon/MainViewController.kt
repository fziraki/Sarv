package abkabk.azbarkon

import abkabk.azbarkon.core.di.initKoinIfNeeded
import abkabk.azbarkon.domain.platform.DailyBeytNotificationScheduler
import androidx.compose.ui.window.ComposeUIViewController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private object IosAppBootstrap : KoinComponent {
    private val dailyBeytNotificationScheduler: DailyBeytNotificationScheduler by inject()

    fun onLaunch() {
        initKoinIfNeeded()
        Napier.base(DebugAntilog())
        dailyBeytNotificationScheduler.rescheduleIfEnabled()
    }
}

fun MainViewController() =
    ComposeUIViewController {
        IosAppBootstrap.onLaunch()
        App()
    }
