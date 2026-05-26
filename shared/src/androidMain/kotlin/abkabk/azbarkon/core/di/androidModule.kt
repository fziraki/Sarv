package abkabk.azbarkon.core.di

import abkabk.azbarkon.core.local.DatabaseDriverFactory
import com.azbarkon.db.AzbarKonDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {

    single {
        DatabaseDriverFactory(
            context = androidContext()
        )
    }

    single {
        AzbarKonDatabase(
            driver = get<DatabaseDriverFactory>().createDriver()
        )
    }
}