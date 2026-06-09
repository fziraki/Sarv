package abkabk.azbarkon.core.di

import com.azbarkon.db.AzbarKonDatabase
import com.azbarkon.db.CatQueries
import com.azbarkon.db.PoemQueries
import com.azbarkon.db.PoetQueries
import com.azbarkon.db.SearchQueries
import com.azbarkon.db.VerseQueries
import org.koin.dsl.module

val databaseModule =
    module {

        single<PoetQueries> {
            get<AzbarKonDatabase>().poetQueries
        }

        single<CatQueries> {
            get<AzbarKonDatabase>().catQueries
        }

        single<PoemQueries> {
            get<AzbarKonDatabase>().poemQueries
        }

        single<VerseQueries> {
            get<AzbarKonDatabase>().verseQueries
        }

        single<SearchQueries> {
            get<AzbarKonDatabase>().searchQueries
        }
    }
