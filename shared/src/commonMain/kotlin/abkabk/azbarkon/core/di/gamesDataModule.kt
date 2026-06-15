package abkabk.azbarkon.core.di

import abkabk.azbarkon.data.local.SqlDelightGamesLocalDataSource
import abkabk.azbarkon.data.repository.OfflineFirstGamesRepository
import abkabk.azbarkon.domain.datasource.GamesLocalDataSource
import abkabk.azbarkon.domain.repository.GamesRepository
import org.koin.dsl.module

val gamesDataModule =
    module {
        single<GamesLocalDataSource> {
            SqlDelightGamesLocalDataSource(
                verseQueries = get(),
                poemQueries = get(),
                poetQueries = get(),
            )
        }
        single<GamesRepository> {
            OfflineFirstGamesRepository(
                localDataSource = get(),
            )
        }
    }
