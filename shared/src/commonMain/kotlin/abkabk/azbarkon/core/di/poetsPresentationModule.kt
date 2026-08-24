package abkabk.azbarkon.core.di

import abkabk.azbarkon.features.poems.details.PoemDetailViewModel
import abkabk.azbarkon.features.mypoems.MyPoemsViewModel
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
                poetDownloadRepository = get(),
                random = Random.Default,
            )
        }
        viewModel { parameters ->
            PoetDetailViewModel(
                poetRepository = get(),
                getRandomGhazalForPoet = get(),
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
                buildShareText = get(),
                startMemorizationFromPoem = get(),
                poemId = parameters.get(),
                player = get()
            )
        }
        viewModel {
            MyPoemsViewModel(
                savedPoemRepository = get(),
                getMyPoems = get(),
            )
        }
    }
