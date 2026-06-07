package abkabk.azbarkon.core.di

import abkabk.azbarkon.core.local.DatabaseDriverFactory
import abkabk.azbarkon.core.platform.ClipboardManager
import abkabk.azbarkon.core.platform.KeyValueStore
import abkabk.azbarkon.core.platform.ShareManager
import com.azbarkon.db.AzbarKonDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidPlatformModule =
    module {
        single {
            DatabaseDriverFactory(
                context = androidContext(),
            )
        }

        single {
            AzbarKonDatabase(
                driver = get<DatabaseDriverFactory>().createDriver(),
            )
        }

        single {
            KeyValueStore(context = androidContext())
        }

        single {
            ClipboardManager(context = androidContext())
        }

        single {
            ShareManager(context = androidContext())
        }
    }
