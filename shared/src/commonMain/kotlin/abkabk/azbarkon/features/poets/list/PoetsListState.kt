package abkabk.azbarkon.features.poets.list

import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.core.uidata.UiText
import abkabk.azbarkon.features.poets.FeaturedPoetUi
import abkabk.azbarkon.features.poets.PoetListItemUi
import androidx.compose.runtime.Stable

@Stable
data class PoetsListState(
    val screenState: UiScreenState = UiScreenState.Idle,
    val searchQuery: String = "",
    val poets: List<PoetListItemUi> = emptyList(),
    val featuredPoet: FeaturedPoetUi? = null,
    val downloadingPoetIds: Set<Int> = emptySet(),
)

sealed interface PoetsListAction {
    data object OnLoad : PoetsListAction

    data object OnRetryClick : PoetsListAction

    data class OnSearchQueryChange(
        val query: String,
    ) : PoetsListAction

    data class OnPoetClick(
        val poetId: Int,
    ) : PoetsListAction

    data class OnDownloadPoet(
        val poetId: Int,
    ) : PoetsListAction

    data object OnFeaturedPoetClick : PoetsListAction

    data object OnScreenEnter : PoetsListAction

    data class OnChatClick(
        val poetId: Int,
    ) : PoetsListAction
}

sealed interface PoetsListEvent {
    data class NavigateToPoetDetail(
        val poetId: Int,
    ) : PoetsListEvent

    data class NavigateToChat(
        val poetId: Int,
    ) : PoetsListEvent

    data class ShowSnackbar(
        val message: UiText,
    ) : PoetsListEvent
}
