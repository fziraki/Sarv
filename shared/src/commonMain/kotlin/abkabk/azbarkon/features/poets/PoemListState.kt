package abkabk.azbarkon.features.poets

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.UiText
import androidx.compose.runtime.Stable

@Stable
data class PoemListState(
    val screenState: UiScreenState = UiScreenState.Idle,
    val title: String = "",
    val poems: List<PoemListItemUi> = emptyList(),
)

@Stable
data class PoemListItemUi(
    val id: Int,
    val title: String,
)

sealed interface PoemListAction {
    data object OnLoad : PoemListAction

    data object OnRetryClick : PoemListAction
}

sealed interface PoemListEvent {
    data class ShowSnackbar(
        val message: UiText,
    ) : PoemListEvent
}
