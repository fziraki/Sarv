package abkabk.azbarkon.features.memorization.select

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.UiText
import androidx.compose.runtime.Stable

@Stable
data class MemorizationSelectState(
    val screenState: UiScreenState = UiScreenState.Idle,
    val activePoemCount: Int = 0,
)

sealed interface MemorizationSelectAction {

    data object OnBackClick : MemorizationSelectAction

    data object OnBabaTaherClick : MemorizationSelectAction

    data object OnHafezGhazalsClick : MemorizationSelectAction

    data object OnSimplePoemClick : MemorizationSelectAction

    data object OnTreasuryClick : MemorizationSelectAction

    data object OnSearchClick : MemorizationSelectAction
    data object OnActivePoemsClick : MemorizationSelectAction
}

sealed interface MemorizationSelectEvent {
    data object NavigateBack : MemorizationSelectEvent

    data class NavigateToPoetDetail(
        val poetId: Int,
    ) : MemorizationSelectEvent

    data class NavigateToPoemList(
        val catId: Int,
        val title: String,
    ) : MemorizationSelectEvent

    data object NavigateToTreasury : MemorizationSelectEvent

    data object NavigateToSearch : MemorizationSelectEvent

    data object NavigateToActivePoems : MemorizationSelectEvent

    data class ShowSnackbar(
        val message: UiText,
    ) : MemorizationSelectEvent
}
