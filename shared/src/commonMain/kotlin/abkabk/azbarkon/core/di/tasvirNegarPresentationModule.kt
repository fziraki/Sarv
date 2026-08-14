package abkabk.azbarkon.core.di

import abkabk.azbarkon.features.tasvirNegar.TasvirNegarViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val tasvirNegarPresentationModule =
    module {
        viewModel { parameters ->
            TasvirNegarViewModel(
                poemRepository = get(),
                shareService = get(),
                imageExportService = get(),
                poemId = parameters.getOrNull<Int>(),
                initialText = parameters.getOrNull<String>(),
            )
        }
    }
