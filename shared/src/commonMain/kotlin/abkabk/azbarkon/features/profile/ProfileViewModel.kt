package abkabk.azbarkon.features.profile

import abkabk.azbarkon.core.domain.result.onFailure
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.ui_base.BaseViewModel
import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.toUiText
import abkabk.azbarkon.domain.repository.UserRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
) : BaseViewModel<ProfileAction, ProfileState, ProfileEvent>(
        initialState = ProfileState(),
    ) {
    init {
        onAction(ProfileAction.OnLoad)
    }

    override fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.OnLoad,
            ProfileAction.OnRetryClick,
            -> loadUserInfo()
        }
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            setState {
                copy(screenState = UiScreenState.Loading)
            }

            userRepository.getUserInfo()
                .onSuccess { userInfo ->
                    setState {
                        copy(
                            screenState = UiScreenState.Success,
                            userInfo = userInfo,
                        )
                    }
                }.onFailure { error ->
                    val message = error.toUiText()
                    setState {
                        copy(
                            screenState =
                                UiScreenState.Error(
                                    message = message,
                                ),
                        )
                    }
                    sendEvent(ProfileEvent.ShowSnackbar(message))
                }
        }
    }
}
