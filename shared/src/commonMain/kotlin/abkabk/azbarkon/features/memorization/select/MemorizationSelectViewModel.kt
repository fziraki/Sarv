package abkabk.azbarkon.features.memorization.select

import abkabk.azbarkon.core.ui_base.BaseViewModel
import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.domain.repository.MemorizationRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MemorizationSelectViewModel(
    private val memorizationRepository: MemorizationRepository,
) : BaseViewModel<MemorizationSelectAction, MemorizationSelectState, MemorizationSelectEvent>(
        initialState = MemorizationSelectState(),
    ) {
    init {
        onAction(MemorizationSelectAction.OnLoad)
    }

    override fun onAction(action: MemorizationSelectAction) {
        when (action) {
            MemorizationSelectAction.OnLoad -> {
                setState { copy(screenState = UiScreenState.Success) }
            }

            MemorizationSelectAction.OnBackClick -> {
                viewModelScope.launch { sendEvent(MemorizationSelectEvent.NavigateBack) }
            }

            MemorizationSelectAction.OnBabaTaherClick -> {
                viewModelScope.launch {
                    val target =
                        memorizationRepository.resolveQuickStart(
                            poetNameFragment = "بابا طاهر",
                            categoryTextFragment = "دوبیتی",
                        )
                    navigateQuickStart(target)
                }
            }

            MemorizationSelectAction.OnHafezGhazalsClick -> {
                viewModelScope.launch {
                    val target =
                        memorizationRepository.resolveQuickStart(
                            poetNameFragment = "حافظ",
                            categoryTextFragment = "غزلیات",
                        )
                    navigateQuickStart(target)
                }
            }

            MemorizationSelectAction.OnTreasuryClick -> {
                viewModelScope.launch { sendEvent(MemorizationSelectEvent.NavigateToTreasury) }
            }

            MemorizationSelectAction.OnSearchClick -> {
                viewModelScope.launch { sendEvent(MemorizationSelectEvent.NavigateToSearch) }
            }

            MemorizationSelectAction.OnActivePoemsClick -> {
                viewModelScope.launch { sendEvent(MemorizationSelectEvent.NavigateToActivePoems) }
            }
        }
    }

    private suspend fun navigateQuickStart(
        target: abkabk.azbarkon.domain.model.memorization.QuickStartTarget,
    ) {
        when {
            target.catId != null && target.catTitle != null ->
                sendEvent(
                    MemorizationSelectEvent.NavigateToPoemList(
                        catId = target.catId,
                        title = target.catTitle,
                    ),
                )
            target.poetId != null ->
                sendEvent(MemorizationSelectEvent.NavigateToPoetDetail(target.poetId))
            else -> sendEvent(MemorizationSelectEvent.NavigateToTreasury)
        }
    }
}
