package abkabk.azbarkon.features.poets

import abkabk.azbarkon.core.domain.result.onFailure
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.ui_base.BaseViewModel
import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.toUiText
import abkabk.azbarkon.domain.repository.PoemRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PoemListViewModel(
    private val poemRepository: PoemRepository,
    private val catId: Int,
    private val title: String,
) : BaseViewModel<PoemListAction, PoemListState, PoemListEvent>(
        initialState = PoemListState(title = title),
    ) {
    init {
        onAction(PoemListAction.OnLoad)
    }

    override fun onAction(action: PoemListAction) {
        when (action) {
            PoemListAction.OnLoad,
            PoemListAction.OnRetryClick,
            -> loadPoems()
        }
    }

    private fun loadPoems() {
        viewModelScope.launch {
            setState { copy(screenState = UiScreenState.Loading) }

            poemRepository.getPoemsByCatId(catId)
                .onSuccess { poems ->
                    setState {
                        copy(
                            screenState = UiScreenState.Success,
                            title = title,
                            poems =
                                poems.map { poem ->
                                    PoemListItemUi(
                                        id = poem.id,
                                        title = poem.title,
                                    )
                                },
                        )
                    }
                }.onFailure { error ->
                    val message = error.toUiText()
                    setState {
                        copy(
                            screenState = UiScreenState.Error(message = message),
                        )
                    }
                    sendEvent(PoemListEvent.ShowSnackbar(message))
                }
        }
    }
}
