package abkabk.azbarkon.core.di

import com.azbarkon.db.AzbarKonDatabase
import com.azbarkon.db.CatQueries
import com.azbarkon.db.PoetQueries
import org.koin.dsl.module

val databaseModule =
    module {

        single<PoetQueries> {
            get<AzbarKonDatabase>().poetQueries
        }

        single<CatQueries> {
            get<AzbarKonDatabase>().catQueries
        }
    }
