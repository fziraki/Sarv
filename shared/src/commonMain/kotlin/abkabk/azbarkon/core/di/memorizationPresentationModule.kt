package abkabk.azbarkon.core.di

import abkabk.azbarkon.features.memorization.active.ActiveMemorizationViewModel
import abkabk.azbarkon.features.memorization.practice.MemorizationPracticeViewModel
import abkabk.azbarkon.features.memorization.select.MemorizationSelectViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val memorizationPresentationModule =
    module {
        viewModelOf(::MemorizationSelectViewModel)
        viewModelOf(::ActiveMemorizationViewModel)
        viewModel { parameters ->
            MemorizationPracticeViewModel(
                memorizationRepository = get(),
                poemId = parameters.getOrNull(),
            )
        }
    }
