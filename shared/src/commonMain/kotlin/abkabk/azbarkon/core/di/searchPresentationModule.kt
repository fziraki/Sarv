package abkabk.azbarkon.core.di

import abkabk.azbarkon.features.search.SearchNavigationArgs
import abkabk.azbarkon.features.search.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val searchPresentationModule =
    module {
        viewModel { parameters ->
            val args = parameters.get<SearchNavigationArgs>()
            SearchViewModel(
                searchRepository = get(),
                poetRepository = get(),
                initialPoetId = args.poetId,
                initialCatId = args.catId,
            )
        }
    }
