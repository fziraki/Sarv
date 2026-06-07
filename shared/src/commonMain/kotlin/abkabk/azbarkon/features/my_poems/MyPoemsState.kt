package abkabk.azbarkon.features.my_poems

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.UiText
import androidx.compose.runtime.Stable

@Stable
data class MyPoemsState(
    val screenState: UiScreenState = UiScreenState.Idle,
    val selectedTab: MyPoemsTab = MyPoemsTab.Liked,
    val likedGroups: List<PoetGroupUi> = emptyList(),
    val bookmarkedGroups: List<PoetGroupUi> = emptyList(),
    val showClearDialog: Boolean = false,
) {
    val activeGroups: List<PoetGroupUi>
        get() =
            when (selectedTab) {
                MyPoemsTab.Liked -> likedGroups
                MyPoemsTab.Bookmarked -> bookmarkedGroups
            }

    val isActiveTabEmpty: Boolean
        get() = activeGroups.isEmpty()
}

sealed interface MyPoemsAction {
    data object OnLoad : MyPoemsAction

    data object OnResume : MyPoemsAction

    data object OnRetryClick : MyPoemsAction

    data class OnTabSelected(
        val tab: MyPoemsTab,
    ) : MyPoemsAction

    data class OnPoemClick(
        val poemId: Int,
    ) : MyPoemsAction

    data class OnRemovePoem(
        val poemId: Int,
    ) : MyPoemsAction

    data object OnClearAllClick : MyPoemsAction

    data object OnClearAllConfirm : MyPoemsAction

    data object OnClearAllDismiss : MyPoemsAction
}

sealed interface MyPoemsEvent {
    data class ShowSnackbar(
        val message: UiText,
    ) : MyPoemsEvent

    data class NavigateToPoemDetail(
        val poemId: Int,
    ) : MyPoemsEvent
}
