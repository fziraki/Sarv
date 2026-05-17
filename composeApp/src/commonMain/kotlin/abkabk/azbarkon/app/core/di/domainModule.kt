package abkabk.azbarkon.app.core.di

import abkabk.azbarkon.app.domain.usecase.GetPoetsUseCase
import org.koin.dsl.module

val domainModule = module {

    factory {
        GetPoetsUseCase(get())
    }
}