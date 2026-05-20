package abkabk.azbarkon.app.features.home

import abkabk.azbarkon.app.core.presentation.UiScreenState
import abkabk.azbarkon.app.domain.model.Poet

class HomeContract {

    data class State(
        val screenState: UiScreenState = UiScreenState.Idle,
        val poets: List<Poet> = emptyList(),
        val isNewMemorization: Boolean = true,
    )

    sealed interface Event {
        data object LoadPoets : Event
        data object LoadTodayPoem : Event
        data object Retry : Event
    }

    sealed interface Effect {
        data class ShowSnackbar(val message: String) : Effect
    }
}