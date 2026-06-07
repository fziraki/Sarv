package abkabk.azbarkon.core.di

import abkabk.azbarkon.features.poets.PoetDetailViewModel
import abkabk.azbarkon.features.poets.PoemListViewModel
import abkabk.azbarkon.features.poets.PoetsListViewModel
import kotlin.random.Random
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val poetsPresentationModule =
    module {
        viewModel {
            PoetsListViewModel(
                poetRepository = get(),
                random = Random.Default,
            )
        }
        viewModel { parameters ->
            PoetDetailViewModel(
                poetRepository = get(),
                poetId = parameters.get(),
            )
        }
        viewModel { parameters ->
            PoemListViewModel(
                poemRepository = get(),
                catId = parameters.get(),
                title = parameters.get(),
            )
        }
    }
