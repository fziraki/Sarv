package abkabk.azbarkon.core.di

import abkabk.azbarkon.features.poems.details.PoemDetailViewModel
import abkabk.azbarkon.features.my_poems.MyPoemsViewModel
import abkabk.azbarkon.features.poems.list.PoemListViewModel
import abkabk.azbarkon.features.poets.details.PoetDetailViewModel
import abkabk.azbarkon.features.poets.list.PoetsListViewModel
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
        viewModel { parameters ->
            PoemDetailViewModel(
                poemRepository = get(),
                savedPoemRepository = get(),
                memorizationRepository = get(),
                shareService = get(),
                poemId = parameters.get(),
            )
        }
        viewModel {
            MyPoemsViewModel(
                poemRepository = get(),
                savedPoemRepository = get(),
            )
        }
    }
