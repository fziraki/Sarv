package abkabk.azbarkon.features.memorization.active

import abkabk.azbarkon.core.uidata.UiScreenState
import androidx.compose.runtime.Stable

@Stable
data class ActiveMemorizationPoemUi(
    val poemId: Int,
    val title: String,
    val poetName: String,
    val boxLevel: Int,
    val level: Int,
    val progress: Float,
    val dueCards: Int,
)

@Stable
data class ActiveMemorizationState(
    val screenState: UiScreenState = UiScreenState.Idle,
    val poems: List<ActiveMemorizationPoemUi> = emptyList(),
    val poemToDelete: Int? = null,
)

sealed interface ActiveMemorizationAction {
    data object OnLoad : ActiveMemorizationAction

    data object OnBackClick : ActiveMemorizationAction

    data object OnAddPoemClick : ActiveMemorizationAction

    data class OnPoemClick(
        val poemId: Int,
    ) : ActiveMemorizationAction

    data class OnDeleteClick(
        val poemId: Int,
    ) : ActiveMemorizationAction

    data object OnDeleteConfirm : ActiveMemorizationAction

    data object OnDeleteDismiss : ActiveMemorizationAction
}

sealed interface ActiveMemorizationEvent {
    data object NavigateBack : ActiveMemorizationEvent

    data object NavigateToSelect : ActiveMemorizationEvent

    data class NavigateToPractice(
        val poemId: Int,
    ) : ActiveMemorizationEvent
}
