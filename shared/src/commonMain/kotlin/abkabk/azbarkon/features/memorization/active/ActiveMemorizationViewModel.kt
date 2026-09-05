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
import sarv.shared.generated.resources.error_db_query
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

            ActiveMemorizationAction.OnResume -> loadPoems()

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
                                copy(screenState = UiScreenState.Error(message = UiText.Resource(Res.string.error_db_query)))
                            }
                        }
                }
            }

            is ActiveMemorizationAction.OnTabSelected -> {
                setState { copy(selectedTab = action.tab) }
            }

            is ActiveMemorizationAction.OnReReviewClick -> {
                viewModelScope.launch {
                    memorizationRepository
                        .resetPoemToActive(action.poemId)
                        .onSuccess {
                            loadPoems()
                        }.onFailure {
                            setState {
                                copy(screenState = UiScreenState.Error(message = UiText.Resource(Res.string.error_db_query)))
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
                    setState { copy(screenState = UiScreenState.Error(UiText.Resource(Res.string.error_db_query))) }
                }

            memorizationRepository
                .getCompletedPoems()
                .onSuccess { poems ->
                    setState {
                        copy(
                            completedPoems = poems.map { it.toUi() },
                        )
                    }
                }
        }
    }
}

private fun ActiveMemorizationPoem.toUi(): ActiveMemorizationPoemUi =
    ActiveMemorizationPoemUi(
        poemId = poemId,
        title = title,
        poetName = poetName,
        reviewCount = reviewCount,
        nextReviewDays = nextReviewDays,
        isCompleted = status == abkabk.azbarkon.domain.model.memorization.ActiveMemorizationStatus.COMPLETED,
        totalCards = totalCards,
        reviewedCards = reviewedCards,
    )
