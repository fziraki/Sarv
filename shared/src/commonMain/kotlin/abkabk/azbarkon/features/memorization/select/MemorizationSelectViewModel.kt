package abkabk.azbarkon.features.memorization.select

import abkabk.azbarkon.core.ui_base.BaseViewModel
import abkabk.azbarkon.domain.repository.MemorizationRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MemorizationSelectViewModel(
    private val memorizationRepository: MemorizationRepository,
) : BaseViewModel<MemorizationSelectAction, MemorizationSelectState, MemorizationSelectEvent>(
        initialState = MemorizationSelectState(),
    ) {
    init {
        observeActiveSummary()
    }

    override fun onAction(action: MemorizationSelectAction) {
        when (action) {

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

            MemorizationSelectAction.OnSimplePoemClick -> {
                viewModelScope.launch {
                    val target =
                        memorizationRepository.resolveQuickStart(
                            poetNameFragment = "سعدی",
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

    private fun observeActiveSummary() {
        memorizationRepository
            .observeActiveSummary()
            .onEach { summary ->
                setState { copy(activePoemCount = summary.activePoemCount) }
            }.launchIn(viewModelScope)
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
