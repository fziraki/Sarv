package abkabk.azbarkon.features.poems.list

import abkabk.azbarkon.core.uidata.UiText
import androidx.compose.runtime.Stable

@Stable
data class PoemListState(
    val title: String = "",
)

@Stable
data class PoemListItemUi(
    val id: Int,
    val title: String,
)

sealed interface PoemListAction {
    data class OnPoemClick(
        val poemId: Int,
    ) : PoemListAction
}

sealed interface PoemListEvent {
    data class ShowSnackbar(
        val message: UiText,
    ) : PoemListEvent

    data class NavigateToPoemDetail(
        val poemId: Int,
    ) : PoemListEvent
}
