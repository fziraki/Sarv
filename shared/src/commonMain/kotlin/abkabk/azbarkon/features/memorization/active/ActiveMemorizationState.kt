package abkabk.azbarkon.features.memorization.active

import abkabk.azbarkon.core.uidata.UiScreenState
import androidx.compose.runtime.Stable

@Stable
data class ActiveMemorizationPoemUi(
    val poemId: Int,
    val title: String,
    val poetName: String,
    val reviewCount: Int,
    val nextReviewDays: Int,
    val isCompleted: Boolean,
    val totalCards: Int = 0,
    val reviewedCards: Int = 0,
)

enum class MemorizationTab {
    ACTIVE,
    COMPLETED,
}

@Stable
data class ActiveMemorizationState(
    val screenState: UiScreenState = UiScreenState.Idle,
    val poems: List<ActiveMemorizationPoemUi> = emptyList(),
    val completedPoems: List<ActiveMemorizationPoemUi> = emptyList(),
    val selectedTab: MemorizationTab = MemorizationTab.ACTIVE,
    val poemToDelete: Int? = null,
)

sealed interface ActiveMemorizationAction {
    data object OnLoad : ActiveMemorizationAction

    data object OnResume : ActiveMemorizationAction

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

    data class OnTabSelected(
        val tab: MemorizationTab,
    ) : ActiveMemorizationAction

    data class OnReReviewClick(
        val poemId: Int,
    ) : ActiveMemorizationAction
}

sealed interface ActiveMemorizationEvent {
    data object NavigateBack : ActiveMemorizationEvent

    data object NavigateToSelect : ActiveMemorizationEvent

    data class NavigateToPractice(
        val poemId: Int,
    ) : ActiveMemorizationEvent
}
