package abkabk.azbarkon.app.core.di

import abkabk.azbarkon.app.features.home.HomeViewModel
import abkabk.azbarkon.app.features.profile.ProfileViewModel
import org.koin.dsl.module

val presentationModule = module {

    factory {
        HomeViewModel(
            getPoetsUseCase = get()
        )
    }

    factory {
        ProfileViewModel(
            getUserInfoUseCase = get()
        )
    }
}