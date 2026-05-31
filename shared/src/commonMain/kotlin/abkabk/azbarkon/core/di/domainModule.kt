package abkabk.azbarkon.core.di

import abkabk.azbarkon.domain.usecase.GetPoetsLocallyUseCase
import abkabk.azbarkon.domain.usecase.GetPoetsUseCase
import abkabk.azbarkon.domain.usecase.GetUserInfoUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule =
    module {
        factoryOf(::GetPoetsLocallyUseCase)
        factoryOf(::GetPoetsUseCase)
        factoryOf(::GetUserInfoUseCase)
    }
