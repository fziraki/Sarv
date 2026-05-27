package abkabk.azbarkon.core.di

import abkabk.azbarkon.data.local.PoetLocalDataSource
import abkabk.azbarkon.data.repository.PoetRepositoryImpl
import abkabk.azbarkon.domain.repository.PoetRepository
import org.koin.dsl.module

val dataModule =
    module {

        single {
            PoetLocalDataSource(
                queries = get(),
            )
        }

        single<PoetRepository> {
            PoetRepositoryImpl(
                api = get(),
                poetLocalDataSource = get(),
            )
        }
    }
