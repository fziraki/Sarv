package abkabk.azbarkon.features.memorization.select

import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.core.uidata.UiText
import androidx.compose.runtime.Stable

@Stable
data class MemorizationSelectState(
    val screenState: UiScreenState = UiScreenState.Idle,
    val activePoemCount: Int = 0,
)

sealed interface MemorizationSelectAction {

    data object OnBackClick : MemorizationSelectAction

    data object OnBabaTaherCoupletsClick : MemorizationSelectAction

    data object OnHafezGhazalsClick : MemorizationSelectAction

    data object OnKhayyamRubaiyatClick : MemorizationSelectAction

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
