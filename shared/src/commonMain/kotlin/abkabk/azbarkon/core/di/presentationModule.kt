package abkabk.azbarkon.core.di

import abkabk.azbarkon.features.home.HomeViewModel
import abkabk.azbarkon.features.profile.ProfileViewModel
import org.koin.dsl.module

val presentationModule =
    module {

        factory {
            HomeViewModel(
                getPoetsLocallyUseCase = get(),
            )
        }

        factory {
            ProfileViewModel(
                getUserInfoUseCase = get(),
            )
        }
    }
