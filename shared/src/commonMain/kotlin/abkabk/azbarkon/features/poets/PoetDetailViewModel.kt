package abkabk.azbarkon.features.poets

import abkabk.azbarkon.core.domain.result.onFailure
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.ui_base.BaseViewModel
import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.toUiText
import abkabk.azbarkon.domain.repository.PoetRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PoetDetailViewModel(
    private val poetRepository: PoetRepository,
    private val poetId: Int,
) : BaseViewModel<PoetDetailAction, PoetDetailState, PoetDetailEvent>(
        initialState = PoetDetailState(),
    ) {
    init {
        onAction(PoetDetailAction.OnLoad)
    }

    override fun onAction(action: PoetDetailAction) {
        when (action) {
            PoetDetailAction.OnLoad,
            PoetDetailAction.OnRetryClick,
            -> loadPoet()

            is PoetDetailAction.OnWorkClick -> Unit
        }
    }

    private fun loadPoet() {
        viewModelScope.launch {
            setState { copy(screenState = UiScreenState.Loading) }

            poetRepository.getPoetWithWorks(poetId)
                .onSuccess { poetWithWorks ->
                    setState {
                        copy(
                            screenState = UiScreenState.Success,
                            name = poetWithWorks.poet.name.orEmpty(),
                            bio = poetWithWorks.poet.description.orEmpty(),
                            imageUrl = poetWithWorks.poet.imageUrl,
                            works = poetWithWorks.toWorkItemsUi(),
                        )
                    }
                }.onFailure { error ->
                    val message = error.toUiText()
                    setState {
                        copy(
                            screenState = UiScreenState.Error(message = message),
                        )
                    }
                    sendEvent(PoetDetailEvent.ShowSnackbar(message))
                }
        }
    }
}
