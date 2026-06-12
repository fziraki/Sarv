package abkabk.azbarkon.core.di

import abkabk.azbarkon.domain.model.games.GameType
import abkabk.azbarkon.features.games.session.GameSessionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val gamesPresentationModule =
    module {
        viewModel { parameters ->
            GameSessionViewModel(
                gameType = parameters.get<GameType>(),
                gamesRepository = get(),
                userPreferencesRepository = get(),
            )
        }
    }
