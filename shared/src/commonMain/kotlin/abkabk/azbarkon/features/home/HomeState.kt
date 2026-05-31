package abkabk.azbarkon.features.home

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.domain.model.Poet
import androidx.compose.runtime.Stable

@Stable
data class HomeState(
    val screenState: UiScreenState = UiScreenState.Idle,
    val poets: List<Poet> = emptyList(),
    val isNewMemorization: Boolean = true,
)

sealed interface HomeAction {
    data object OnLoad : HomeAction

    data object OnRetryClick : HomeAction
}

sealed interface HomeEvent {
    data class ShowSnackbar(
        val message: UiText,
    ) : HomeEvent
}
