package abkabk.azbarkon.app.features.home

import abkabk.azbarkon.app.core.network.ApiResult
import abkabk.azbarkon.app.core.presentation.BaseViewModel
import abkabk.azbarkon.app.core.presentation.UiScreenState
import abkabk.azbarkon.app.core.presentation.UiText
import abkabk.azbarkon.app.domain.usecase.GetPoetsUseCase
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getPoetsUseCase: GetPoetsUseCase
) : BaseViewModel<
        HomeContract.Event,
        HomeContract.State,
        HomeContract.Effect
        >(
        initialState = HomeContract.State()
    ) {

    init {
        onEvent(HomeContract.Event.LoadPoets)
        onEvent(HomeContract.Event.LoadTodayPoem)
    }

    override fun onEvent(event: HomeContract.Event) {
        when (event) {

            HomeContract.Event.LoadPoets -> {
                loadPoets()
            }

            HomeContract.Event.LoadTodayPoem -> {
//                loadTodayPoem()
            }

            HomeContract.Event.Retry -> {
                loadPoets()
            }
        }
    }

    private fun loadPoets() {
        viewModelScope.launch {

            setState {
                copy(
                    screenState = UiScreenState.Loading
                )
            }

            when (val result = getPoetsUseCase()) {

                is ApiResult.Success -> {


                    setState {
                        copy(
                            screenState = UiScreenState.Success,
                            poets = result.data
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
                        HomeContract.Effect.ShowSnackbar(
                            result.message
                        )
                    )
                }
            }
        }
    }

//    private fun loadTodayPoem() {
//        viewModelScope.launch {
//
//            setState {
//                copy(
//                    screenState = UiScreenState.Loading
//                )
//            }
//
//            when (val result = getTodayPoemUseCase()) {
//
//                is ApiResult.Success -> {
//
//                    setState {
//                        copy(
//                            screenState = UiScreenState.Success,
//                            poets = result.data
//                        )
//                    }
//                }
//
//                is ApiResult.Error -> {
//
//                    setState {
//                        copy(
//                            screenState = UiScreenState.Error(
//                                message = UiText.Dynamic(result.message)
//                            ),
//                        )
//                    }
//
//                    sendEffect(
//                        HomeContract.Effect.ShowSnackbar(
//                            result.message
//                        )
//                    )
//                }
//            }
//        }
//    }
}