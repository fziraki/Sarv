package abkabk.azbarkon.features.memorization.active

import abkabk.azbarkon.core.domain.result.onFailure
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.uidata.BaseViewModel
import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.core.uidata.UiText
import abkabk.azbarkon.domain.model.memorization.ActiveMemorizationPoem
import abkabk.azbarkon.domain.repository.MemorizationRepository
import androidx.lifecycle.viewModelScope
import sarv.shared.generated.resources.Res
import sarv.shared.generated.resources.error_unknown
import kotlinx.coroutines.launch

class ActiveMemorizationViewModel(
    private val memorizationRepository: MemorizationRepository,
) : BaseViewModel<ActiveMemorizationAction, ActiveMemorizationState, ActiveMemorizationEvent>(
        initialState = ActiveMemorizationState(),
    ) {
    init {
        onAction(ActiveMemorizationAction.OnLoad)
    }

    override fun onAction(action: ActiveMemorizationAction) {
        when (action) {
            ActiveMemorizationAction.OnLoad -> loadPoems()

            ActiveMemorizationAction.OnBackClick -> {
                viewModelScope.launch { sendEvent(ActiveMemorizationEvent.NavigateBack) }
            }

            ActiveMemorizationAction.OnAddPoemClick -> {
                viewModelScope.launch { sendEvent(ActiveMemorizationEvent.NavigateToSelect) }
            }

            is ActiveMemorizationAction.OnPoemClick -> {
                viewModelScope.launch {
                    sendEvent(ActiveMemorizationEvent.NavigateToPractice(action.poemId))
                }
            }

            is ActiveMemorizationAction.OnDeleteClick -> {
                setState { copy(poemToDelete = action.poemId) }
            }

            ActiveMemorizationAction.OnDeleteDismiss -> {
                setState { copy(poemToDelete = null) }
            }

            ActiveMemorizationAction.OnDeleteConfirm -> {
                val poemId = state.value.poemToDelete ?: return
                viewModelScope.launch {
                    memorizationRepository
                        .removePoem(poemId)
                        .onSuccess {
                            setState { copy(poemToDelete = null) }
                            loadPoems()
                        }.onFailure {
                            setState {
                                copy(screenState = UiScreenState.Error(message = UiText.Resource(Res.string.error_unknown)))
                            }
                        }
                }
            }
        }
    }

    private fun loadPoems() {
        viewModelScope.launch {
            setState { copy(screenState = UiScreenState.Loading) }
            memorizationRepository
                .getActivePoems()
                .onSuccess { poems ->
                    setState {
                        copy(
                            screenState = UiScreenState.Success,
                            poems = poems.map { it.toUi() },
                        )
                    }
                }.onFailure {
                    setState { copy(screenState = UiScreenState.Error(UiText.Resource(Res.string.error_unknown))) }
                }
        }
    }
}

private fun ActiveMemorizationPoem.toUi(): ActiveMemorizationPoemUi {
    val progress =
        if (totalCards == 0) {
            0f
        } else {
            reviewedCards.toFloat() / totalCards.toFloat()
        }
    return ActiveMemorizationPoemUi(
        poemId = poemId,
        title = title,
        poetName = poetName,
        boxLevel = boxLevel,
        level = level,
        progress = progress,
        dueCards = dueCards,
    )
}
