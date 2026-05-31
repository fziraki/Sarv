package abkabk.azbarkon.core.di

import abkabk.azbarkon.core.local.DatabaseDriverFactory
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
    }
