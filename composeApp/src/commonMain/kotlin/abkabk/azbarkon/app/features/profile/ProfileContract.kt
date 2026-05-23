package abkabk.azbarkon.app.features.profile

import abkabk.azbarkon.app.core.presentation.UiScreenState
import abkabk.azbarkon.app.domain.model.UserInfo

class ProfileContract {


    data class State(
        val screenState: UiScreenState = UiScreenState.Idle,
        val userInfo: UserInfo? = null
    )

    sealed interface Event {
        data object LoadUserInfo : Event
        data object Retry : Event
    }

    sealed interface Effect {
        data class ShowSnackbar(val message: String) : Effect
    }
}