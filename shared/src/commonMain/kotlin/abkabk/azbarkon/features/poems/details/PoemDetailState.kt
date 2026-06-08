package abkabk.azbarkon.features.poems.details

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.UiText
import androidx.compose.runtime.Stable

@Stable
data class PoemDetailState(
    val screenState: UiScreenState = UiScreenState.Idle,
    val poetName: String = "",
    val subtitle: String = "",
    val verses: List<PoemVerseUi> = emptyList(),
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
)

sealed interface PoemDetailAction {
    data object OnLoad : PoemDetailAction

    data object OnRetryClick : PoemDetailAction

    data object OnSearchClick : PoemDetailAction

    data object OnShareClick : PoemDetailAction

    data object OnLikeClick : PoemDetailAction

    data object OnBookmarkClick : PoemDetailAction

    data object OnImageCreatorClick : PoemDetailAction

    data object OnMemorizeClick : PoemDetailAction
}

sealed interface PoemDetailEvent {
    data class ShowSnackbar(
        val message: UiText,
    ) : PoemDetailEvent
}
