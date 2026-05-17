package abkabk.azbarkon.app.core.di

import abkabk.azbarkon.app.data.repository.PoetRepositoryImpl
import abkabk.azbarkon.app.domain.repository.PoetRepository
import org.koin.dsl.module

val dataModule = module {

    single<PoetRepository> {
        PoetRepositoryImpl(get())
    }
}