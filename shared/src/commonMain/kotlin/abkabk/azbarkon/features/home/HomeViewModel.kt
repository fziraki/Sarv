package abkabk.azbarkon.features.home

import abkabk.azbarkon.core.network.ApiResult
import abkabk.azbarkon.core.ui_base.BaseViewModel
import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.domain.usecase.GetPoetsLocallyUseCase
import abkabk.azbarkon.domain.usecase.GetPoetsUseCase
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getPoetsUseCase: GetPoetsUseCase,
    private val getPoetsLocallyUseCase: GetPoetsLocallyUseCase
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
                loadPoetsLocally()
            }

            HomeContract.Event.LoadTodayPoem -> {
//                loadTodayPoem()
            }

            HomeContract.Event.Retry -> {
                loadPoetsLocally()
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

    private fun loadPoetsLocally() {

        viewModelScope.launch {
            val result = getPoetsLocallyUseCase()
            Napier.d("result ${result.size}")
            setState {
                copy(
                    screenState = UiScreenState.Success,
                    poets = result
                )
            }
        }


    }


}