package abkabk.azbarkon.core.di

import abkabk.azbarkon.data.local.SqlDelightPoemLocalDataSource
import abkabk.azbarkon.data.repository.OfflineFirstPoemRepository
import abkabk.azbarkon.domain.datasource.PoemLocalDataSource
import abkabk.azbarkon.domain.repository.PoemRepository
import org.koin.dsl.module

val poemDataModule =
    module {
        single<PoemLocalDataSource> {
            SqlDelightPoemLocalDataSource(
                poemQueries = get(),
            )
        }
        single<PoemRepository> {
            OfflineFirstPoemRepository(
                localDataSource = get(),
            )
        }
    }
