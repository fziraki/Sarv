package abkabk.azbarkon.app.core.di

import abkabk.azbarkon.app.domain.usecase.GetPoetsUseCase
import abkabk.azbarkon.app.domain.usecase.GetUserInfoUseCase
import org.koin.dsl.module

val domainModule = module {

    factory {
        GetPoetsUseCase(get())
    }

    factory {
        GetUserInfoUseCase(get())
    }
}