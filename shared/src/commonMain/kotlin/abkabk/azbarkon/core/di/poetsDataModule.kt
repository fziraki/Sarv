package abkabk.azbarkon.core.di

import abkabk.azbarkon.data.local.SqlDelightPoetLocalDataSource
import abkabk.azbarkon.data.remote.KtorPoetRemoteDataSource
import abkabk.azbarkon.data.repository.OfflineFirstPoetRepository
import abkabk.azbarkon.domain.datasource.PoetLocalDataSource
import abkabk.azbarkon.domain.datasource.PoetRemoteDataSource
import abkabk.azbarkon.domain.repository.PoetRepository
import org.koin.dsl.module

val poetsDataModule =
    module {
        single<PoetLocalDataSource> {
            SqlDelightPoetLocalDataSource(get())
        }
        single<PoetRemoteDataSource> {
            KtorPoetRemoteDataSource(get())
        }
        single<PoetRepository> {
            OfflineFirstPoetRepository(
                localDataSource = get(),
                remoteDataSource = get(),
            )
        }
    }
