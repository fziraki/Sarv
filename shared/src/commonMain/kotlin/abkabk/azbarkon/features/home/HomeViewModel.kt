package abkabk.azbarkon.features.home

import abkabk.azbarkon.core.ui_base.BaseViewModel
import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.domain.usecase.GetPoetsLocallyUseCase
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getPoetsLocallyUseCase: GetPoetsLocallyUseCase,
) : BaseViewModel<
        HomeContract.Event,
        HomeContract.State,
        HomeContract.Effect,
    >(
        initialState = HomeContract.State(),
    ) {
    init {
        onEvent(HomeContract.Event.LoadPoets)
    }

    override fun onEvent(event: HomeContract.Event) {
        when (event) {
            HomeContract.Event.LoadPoets,
            HomeContract.Event.Retry,
            -> {
                loadPoetsLocally()
            }
        }
    }

    private fun loadPoetsLocally() {
        viewModelScope.launch {
            setState {
                copy(
                    screenState = UiScreenState.Loading,
                )
            }

            runCatching { getPoetsLocallyUseCase() }
                .onSuccess { poets ->
                    Napier.d("Loaded ${poets.size} poets from local database")
                    setState {
                        copy(
                            screenState = UiScreenState.Success,
                            poets = poets,
                        )
                    }
                }.onFailure { error ->
                    Napier.e("Failed to load local poets", error)
                    setState {
                        copy(
                            screenState =
                                UiScreenState.Error(
                                    message = UiText.Dynamic(error.message.orEmpty()),
                                ),
                        )
                    }
                    sendEffect(
                        HomeContract.Effect.ShowSnackbar(
                            error.message.orEmpty(),
                        ),
                    )
                }
        }
    }
}
