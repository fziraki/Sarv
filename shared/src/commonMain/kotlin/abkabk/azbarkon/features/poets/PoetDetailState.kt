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
    val works: List<PoetWorkItemUi> = emptyList(),
)

sealed interface PoetDetailAction {
    data object OnLoad : PoetDetailAction

    data object OnRetryClick : PoetDetailAction

    data class OnWorkClick(
        val workId: Int,
    ) : PoetDetailAction
}

sealed interface PoetDetailEvent {
    data class ShowSnackbar(
        val message: UiText,
    ) : PoetDetailEvent
}
