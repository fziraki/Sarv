package abkabk.azbarkon.app.core.di

import abkabk.azbarkon.app.features.home.HomeViewModel
import org.koin.dsl.module

val presentationModule = module {

    factory {
        HomeViewModel(
            getPoetsUseCase = get()
        )
    }
}