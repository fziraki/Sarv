package abkabk.azbarkon.core.di

import abkabk.azbarkon.features.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homePresentationModule =
    module {
        viewModelOf(::HomeViewModel)
    }
