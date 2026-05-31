package abkabk.azbarkon.features.home

import abkabk.azbarkon.core.domain.result.onFailure
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.ui_base.BaseViewModel
import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.toUiText
import abkabk.azbarkon.domain.usecase.GetPoetsUseCase
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getPoetsUseCase: GetPoetsUseCase,
) : BaseViewModel<HomeAction, HomeState, HomeEvent>(
        initialState = HomeState(),
    ) {
    init {
        onAction(HomeAction.OnLoad)
    }

    override fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnLoad,
            HomeAction.OnRetryClick,
            -> loadPoets()
        }
    }

    private fun loadPoets() {
        viewModelScope.launch {
            setState {
                copy(screenState = UiScreenState.Loading)
            }

            getPoetsUseCase()
                .onSuccess { poets ->
                    Napier.d("Loaded ${poets.size} poets from local database")
                    setState {
                        copy(
                            screenState = UiScreenState.Success,
                            poets = poets,
                        )
                    }
                }.onFailure { error ->
                    Napier.e("Failed to load poets: $error")
                    val message = error.toUiText()
                    setState {
                        copy(
                            screenState =
                                UiScreenState.Error(
                                    message = message,
                                ),
                        )
                    }
                    sendEvent(HomeEvent.ShowSnackbar(message))
                }
        }
    }
}
