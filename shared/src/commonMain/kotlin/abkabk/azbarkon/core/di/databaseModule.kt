package abkabk.azbarkon.core.di

import com.sarv.db.SarvDatabase
import com.sarv.db.CatQueries
import com.sarv.db.PoemQueries
import com.sarv.db.PoetQueries
import com.sarv.db.SearchQueries
import com.sarv.db.VerseQueries
import org.koin.dsl.module

val databaseModule =
    module {

        single<PoetQueries> {
            get<SarvDatabase>().poetQueries
        }

        single<CatQueries> {
            get<SarvDatabase>().catQueries
        }

        single<PoemQueries> {
            get<SarvDatabase>().poemQueries
        }

        single<VerseQueries> {
            get<SarvDatabase>().verseQueries
        }

        single<SearchQueries> {
            get<SarvDatabase>().searchQueries
        }
    }
