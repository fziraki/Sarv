package abkabk.azbarkon.app.features.profile

import abkabk.azbarkon.app.core.network.ApiResult
import abkabk.azbarkon.app.core.presentation.BaseViewModel
import abkabk.azbarkon.app.core.presentation.UiScreenState
import abkabk.azbarkon.app.core.presentation.UiText
import abkabk.azbarkon.app.domain.usecase.GetUserInfoUseCase
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserInfoUseCase: GetUserInfoUseCase
) : BaseViewModel<
        ProfileContract.Event,
        ProfileContract.State,
        ProfileContract.Effect
        >(
        initialState = ProfileContract.State()
    ) {

    init {
        onEvent(ProfileContract.Event.LoadUserInfo)
    }

    override fun onEvent(event: ProfileContract.Event) {
        when (event) {

            ProfileContract.Event.LoadUserInfo -> {
                loadUserInfo()
            }

            ProfileContract.Event.Retry -> {
                loadUserInfo()
            }
        }
    }

    private fun loadUserInfo() {
        viewModelScope.launch {

            setState {
                copy(
                    screenState = UiScreenState.Loading
                )
            }

            when (val result = getUserInfoUseCase()) {

                is ApiResult.Success -> {

                    setState {
                        copy(
                            screenState = UiScreenState.Success,
                            userInfo = result.data
                        )
                    }
                }

                is ApiResult.Error -> {

                    setState {
                        copy(
                            screenState = UiScreenState.Error(
                                message = UiText.Dynamic(result.message)
                            ),
                        )
                    }

                    sendEffect(
                        ProfileContract.Effect.ShowSnackbar(
                            result.message
                        )
                    )
                }
            }
        }
    }

}