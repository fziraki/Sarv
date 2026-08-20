package abkabk.azbarkon.features.games.session

sealed interface GameSessionAction {
    data object OnBackClick : GameSessionAction

    data object OnHintClick : GameSessionAction

    data object OnCheckAnswerClick : GameSessionAction

    data class OnOptionSelected(
        val index: Int,
    ) : GameSessionAction

    data class OnPoetSelected(
        val poetId: Int,
    ) : GameSessionAction

    data class OnWordSelected(
        val word: String,
    ) : GameSessionAction

    data class OnReorderLines(
        val fromIndex: Int,
        val toIndex: Int,
    ) : GameSessionAction
}
