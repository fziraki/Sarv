package abkabk.azbarkon.features.poets.details

import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.features.poets.PoetCategoryRowUi
import androidx.compose.runtime.Stable

@Stable
data class PoetDetailState(
    val screenState: UiScreenState = UiScreenState.Idle,
    val name: String = "",
    val bio: String = "",
    val imageUrl: String? = null,
    val categories: List<PoetCategoryRowUi> = emptyList(),
    val canChat: Boolean = false,
)

sealed interface PoetDetailAction {
    data object OnLoad : PoetDetailAction

    data class OnCategoryToggle(
        val categoryId: Int,
    ) : PoetDetailAction

    data class OnCategoryClick(
        val categoryId: Int,
        val title: String,
    ) : PoetDetailAction

    data object OnChatClick : PoetDetailAction
}

sealed interface PoetDetailEvent {
    data class NavigateToPoemList(
        val catId: Int,
        val title: String,
    ) : PoetDetailEvent

    data class NavigateToChat(
        val poetId: Int,
    ) : PoetDetailEvent
}
