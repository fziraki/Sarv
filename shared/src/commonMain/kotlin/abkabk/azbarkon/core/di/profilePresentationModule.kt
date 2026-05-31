package abkabk.azbarkon.core.di

import abkabk.azbarkon.features.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profilePresentationModule =
    module {
        viewModelOf(::ProfileViewModel)
    }
