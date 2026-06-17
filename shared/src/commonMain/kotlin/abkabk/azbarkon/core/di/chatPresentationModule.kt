package abkabk.azbarkon.core.di

import abkabk.azbarkon.features.chat.ChatViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val chatPresentationModule =
    module {
        viewModel { parameters ->
            ChatViewModel(
                poetRepository = get(),
                chatRepository = get(),
                clipboardService = get(),
                poetId = parameters.get(),
            )
        }
    }
