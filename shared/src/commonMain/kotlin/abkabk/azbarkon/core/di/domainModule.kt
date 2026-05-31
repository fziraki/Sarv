package abkabk.azbarkon.core.di

import abkabk.azbarkon.domain.usecase.GetPoetsLocallyUseCase
import abkabk.azbarkon.domain.usecase.GetPoetsUseCase
import abkabk.azbarkon.domain.usecase.GetUserInfoUseCase
import org.koin.dsl.module

val domainModule =
    module {

        factory {
            GetPoetsLocallyUseCase(get())
        }

        factory {
            GetPoetsUseCase(get())
        }

        factory {
            GetUserInfoUseCase()
        }
    }
