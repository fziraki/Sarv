package abkabk.azbarkon.core.di

import abkabk.azbarkon.core.local.DatabaseDriverFactory
import abkabk.azbarkon.core.platform.ClipboardManager
import abkabk.azbarkon.core.platform.KeyValueStore
import abkabk.azbarkon.core.platform.ShareManager
import abkabk.azbarkon.data.platform.IosDailyBeytNotificationScheduler
import abkabk.azbarkon.data.platform.IosNotificationPermissionGateway
import abkabk.azbarkon.domain.platform.DailyBeytNotificationScheduler
import abkabk.azbarkon.domain.platform.NotificationPermissionGateway
import com.azbarkon.db.AzbarKonDatabase
import org.koin.dsl.module

val iosPlatformModule =
    module {
        single {
            DatabaseDriverFactory()
        }

        single {
            AzbarKonDatabase(
                driver = get<DatabaseDriverFactory>().createDriver(),
            )
        }

        single {
            KeyValueStore()
        }

        single {
            ClipboardManager()
        }

        single {
            ShareManager()
        }

        single<DailyBeytNotificationScheduler> {
            IosDailyBeytNotificationScheduler(
                dailyBeytRepository = get(),
                userPreferencesRepository = get(),
            )
        }

        single<NotificationPermissionGateway> {
            IosNotificationPermissionGateway()
        }
    }
