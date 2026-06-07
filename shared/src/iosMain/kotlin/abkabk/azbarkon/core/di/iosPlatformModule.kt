package abkabk.azbarkon.core.di

import abkabk.azbarkon.core.local.DatabaseDriverFactory
import abkabk.azbarkon.core.platform.ClipboardManager
import abkabk.azbarkon.core.platform.KeyValueStore
import abkabk.azbarkon.core.platform.ShareManager
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
    }
