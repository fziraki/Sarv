package abkabk.azbarkon.core.di

import abkabk.azbarkon.data.local.SqlDelightPoetLocalDataSource
import abkabk.azbarkon.data.repository.KtorPoetDownloadRepository
import abkabk.azbarkon.data.repository.OfflineFirstPoetRepository
import abkabk.azbarkon.domain.datasource.PoetLocalDataSource
import abkabk.azbarkon.domain.repository.PoetDownloadRepository
import abkabk.azbarkon.domain.repository.PoetRepository
import org.koin.dsl.module

val poetsDataModule =
    module {
        single<PoetLocalDataSource> {
            SqlDelightPoetLocalDataSource(
                poetQueries = get(),
                catQueries = get(),
            )
        }
        single<PoetRepository> {
            OfflineFirstPoetRepository(
                localDataSource = get(),
                poetImagePrefetcher = get(),
            )
        }
        single<PoetDownloadRepository> {
            KtorPoetDownloadRepository(
                httpClient = get(),
                storage = get(),
                poetDbDriverFactory = get(),
                mainDatabase = get(),
                mainDriver = get(),
            )
        }
    }
