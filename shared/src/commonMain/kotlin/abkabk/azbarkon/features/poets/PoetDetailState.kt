package abkabk.azbarkon.features.poets

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.UiText
import androidx.compose.runtime.Stable

@Stable
data class PoetDetailState(
    val screenState: UiScreenState = UiScreenState.Idle,
    val name: String = "",
    val bio: String = "",
    val imageUrl: String? = null,
    val categories: List<PoetCategoryRowUi> = emptyList(),
)

sealed interface PoetDetailAction {
    data object OnLoad : PoetDetailAction

    data object OnRetryClick : PoetDetailAction

    data class OnCategoryToggle(
        val categoryId: Int,
    ) : PoetDetailAction

    data class OnCategoryClick(
        val categoryId: Int,
        val title: String,
    ) : PoetDetailAction
}

sealed interface PoetDetailEvent {
    data class NavigateToPoemList(
        val catId: Int,
        val title: String,
    ) : PoetDetailEvent

    data class ShowSnackbar(
        val message: UiText,
    ) : PoetDetailEvent
}
