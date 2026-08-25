package abkabk.azbarkon.features.games.session

import abkabk.azbarkon.core.uidata.UiText
import abkabk.azbarkon.domain.model.games.GameSessionSummary
import abkabk.azbarkon.domain.model.games.GameType

sealed interface GameSessionEvent {
    data object NavigateBack : GameSessionEvent

    data class NavigateToResult(
        val gameType: GameType,
        val summary: GameSessionSummary,
    ) : GameSessionEvent

    data class ShowSnackbar(val message: UiText) : GameSessionEvent
}
