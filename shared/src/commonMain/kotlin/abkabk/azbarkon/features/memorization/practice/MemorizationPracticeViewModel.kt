package abkabk.azbarkon.features.memorization.practice

import abkabk.azbarkon.core.domain.result.onFailure
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.ui_base.BaseViewModel
import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.core.ui_base.toUiText
import abkabk.azbarkon.domain.model.memorization.MemorizationError
import abkabk.azbarkon.domain.model.memorization.SrsCard
import abkabk.azbarkon.domain.model.memorization.SrsGrade
import abkabk.azbarkon.domain.repository.MemorizationRepository
import abkabk.azbarkon.domain.srs.DiffToken
import abkabk.azbarkon.domain.srs.DiffTokenType
import abkabk.azbarkon.domain.srs.TextDiffHighlighter
import androidx.lifecycle.viewModelScope
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.error_unknown
import kotlinx.coroutines.launch

class MemorizationPracticeViewModel(
    private val memorizationRepository: MemorizationRepository,
    private val poemId: Int?,
) : BaseViewModel<MemorizationPracticeAction, MemorizationPracticeState, MemorizationPracticeEvent>(
        initialState = MemorizationPracticeState(),
    ) {
    private var dueCards: List<SrsCard> = emptyList()
    private var currentQueueIndex = 0

    init {
        onAction(MemorizationPracticeAction.OnLoad)
    }

    override fun onAction(action: MemorizationPracticeAction) {
        when (action) {
            MemorizationPracticeAction.OnLoad,
            MemorizationPracticeAction.OnRetryClick,
            -> loadSession()

            MemorizationPracticeAction.OnBackClick -> {
                viewModelScope.launch { sendEvent(MemorizationPracticeEvent.NavigateBack) }
            }

            MemorizationPracticeAction.OnRevealClick -> revealCard()

            MemorizationPracticeAction.OnToggleTypingMode -> {
                setState { copy(isTypingMode = !isTypingMode, typedAnswer = "") }
            }

            is MemorizationPracticeAction.OnTypedAnswerChange -> {
                setState { copy(typedAnswer = action.answer) }
            }

            MemorizationPracticeAction.OnSubmitTypedAnswer -> evaluateTypedAnswer()

            is MemorizationPracticeAction.OnGradeClick -> submitGrade(action.grade)

            MemorizationPracticeAction.OnNextCard -> advanceToNextCard()
        }
    }

    private fun loadSession() {
        viewModelScope.launch {
            setState { copy(screenState = UiScreenState.Loading) }
            memorizationRepository
                .getDueCards(poemId)
                .onSuccess { cards ->
                    dueCards = cards
                    currentQueueIndex = 0
                    if (cards.isEmpty()) {
                        setState {
                            copy(
                                screenState = UiScreenState.Success,
                                phase = PracticePhase.COMPLETE,
                                currentCard = null,
                                totalCards = 0,
                            )
                        }
                    } else {
                        showCardAt(currentQueueIndex)
                    }
                }.onFailure { error ->
                    val message = error.toMemorizationUiText()
                    setState { copy(screenState = UiScreenState.Error(message)) }
                    sendEvent(MemorizationPracticeEvent.ShowSnackbar(message))
                }
        }
    }

    private fun showCardAt(index: Int) {
        val card = dueCards.getOrNull(index)
        if (card == null) {
            setState {
                copy(
                    screenState = UiScreenState.Success,
                    phase = PracticePhase.COMPLETE,
                    currentCard = null,
                )
            }
            return
        }
        setState {
            copy(
                screenState = UiScreenState.Success,
                phase = PracticePhase.SHOW_FRONT,
                currentCard =
                    PracticeCardUi(
                        id = card.id,
                        front = card.front,
                        back = card.back,
                    ),
                cardIndex = index + 1,
                totalCards = dueCards.size,
                typedAnswer = "",
                diffTokens = emptyList(),
                suggestedGrade = null,
                selectedGrade = null,
            )
        }
    }

    private fun revealCard() {
        if (state.value.phase != PracticePhase.SHOW_FRONT || state.value.isTypingMode) return
        setState { copy(phase = PracticePhase.REVEALED) }
    }

    private fun evaluateTypedAnswer() {
        val card = state.value.currentCard ?: return
        val diff = TextDiffHighlighter.diff(card.back, state.value.typedAnswer)
        val suggested = TextDiffHighlighter.suggestGrade(card.back, state.value.typedAnswer)
        setState {
            copy(
                phase = PracticePhase.FEEDBACK,
                diffTokens = diff,
                suggestedGrade = suggested,
                selectedGrade = suggested,
            )
        }
    }

    private fun submitGrade(grade: SrsGrade) {
        val cardId = state.value.currentCard?.id ?: return
        viewModelScope.launch {
            memorizationRepository
                .submitReview(cardId, grade)
                .onSuccess {
                    when (state.value.phase) {
                        PracticePhase.REVEALED -> {
                            val back = state.value.currentCard?.back.orEmpty()
                            setState {
                                copy(
                                    phase = PracticePhase.FEEDBACK,
                                    diffTokens = allCorrectTokens(back),
                                    selectedGrade = grade,
                                    suggestedGrade = grade,
                                )
                            }
                        }
                        PracticePhase.FEEDBACK -> {
                            setState { copy(selectedGrade = grade) }
                            advanceToNextCard()
                        }
                        else -> Unit
                    }
                }.onFailure { error ->
                    sendEvent(MemorizationPracticeEvent.ShowSnackbar(error.toMemorizationUiText()))
                }
        }
    }

    private fun advanceToNextCard() {
        currentQueueIndex += 1
        showCardAt(currentQueueIndex)
    }

    private fun allCorrectTokens(text: String): List<DiffToken> =
        text
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .map { DiffToken(it, DiffTokenType.CORRECT) }
}

private fun MemorizationError.toMemorizationUiText(): UiText =
    when (this) {
        MemorizationError.MaxActivePoemsReached,
        MemorizationError.PoemNotFound,
        MemorizationError.CardNotFound,
        MemorizationError.Unknown,
        -> UiText.Resource(Res.string.error_unknown)
    }
