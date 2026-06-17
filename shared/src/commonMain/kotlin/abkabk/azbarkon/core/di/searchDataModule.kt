package abkabk.azbarkon.core.di

import abkabk.azbarkon.data.local.SqlDelightSearchLocalDataSource
import abkabk.azbarkon.data.repository.OfflineFirstSearchRepository
import abkabk.azbarkon.domain.datasource.SearchLocalDataSource
import abkabk.azbarkon.domain.repository.SearchRepository
import org.koin.dsl.module

val searchDataModule =
    module {
        single<SearchLocalDataSource> {
            SqlDelightSearchLocalDataSource(
                searchQueries = get(),
                catQueries = get(),
            )
        }
        single<SearchRepository> {
            OfflineFirstSearchRepository(
                localDataSource = get(),
            )
        }
    }
