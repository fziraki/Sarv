package abkabk.azbarkon.features.memorization.practice

import abkabk.azbarkon.core.domain.result.onFailure
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.uidata.BaseViewModel
import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.core.uidata.UiText
import abkabk.azbarkon.domain.model.memorization.MemorizationError
import abkabk.azbarkon.domain.model.memorization.SrsCard
import abkabk.azbarkon.domain.model.memorization.SrsGrade
import abkabk.azbarkon.domain.repository.MemorizationRepository
import abkabk.azbarkon.domain.srs.CardGenerator
import abkabk.azbarkon.domain.srs.TextDiffHighlighter
import androidx.lifecycle.viewModelScope
import sarv.shared.generated.resources.Res
import sarv.shared.generated.resources.error_unknown
import sarv.shared.generated.resources.memorization_review_notification_enabled
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
            MemorizationPracticeAction.OnLoad -> loadSession()

            MemorizationPracticeAction.OnBackClick -> {
                viewModelScope.launch { sendEvent(MemorizationPracticeEvent.NavigateBack) }
            }

            MemorizationPracticeAction.OnRevealClick -> revealCard()

            MemorizationPracticeAction.OnTypingModeClick -> enableTypingMode()

            is MemorizationPracticeAction.OnTypedAnswerChange -> {
                setState { copy(typedAnswer = action.answer) }
            }

            MemorizationPracticeAction.OnSubmitTypedAnswer -> evaluateTypedAnswer()

            is MemorizationPracticeAction.OnGradeClick -> selectGrade(action.grade)

            MemorizationPracticeAction.OnNextCard -> submitAndAdvance()

            MemorizationPracticeAction.OnNotificationPermissionGranted -> {
                setState {
                    copy(
                        screenState = UiScreenState.Error(
                            message = UiText.Resource(Res.string.memorization_review_notification_enabled),
                            isSuccess = true,
                        ),
                    )
                }
            }
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
                                sessionMistakes = 0,
                                sessionReviewed = 0,
                                sessionLearned = 0,
                            )
                        }
                    } else {
                        setState {
                            copy(
                                sessionMistakes = 0,
                                sessionReviewed = 0,
                                sessionLearned = 0,
                            )
                        }
                        showCardAt(currentQueueIndex)
                    }
                }.onFailure { error ->
                    val message = error.toMemorizationUiText()
                    setState { copy(screenState = UiScreenState.Error(message)) }
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
                        expectedContinuation =
                            CardGenerator.expectedContinuation(
                                front = card.front,
                                back = card.back,
                            ),
                    ),
                cardIndex = index + 1,
                totalCards = dueCards.size,
                isTypingMode = false,
                typedAnswer = "",
                diffTokens = emptyList(),
                suggestedGrade = null,
                selectedGrade = null,
                gradesLocked = false,
            )
        }
    }

    private fun revealCard() {
        if (state.value.phase != PracticePhase.SHOW_FRONT) return
        setState {
            copy(
                phase = PracticePhase.REVEALED,
                isTypingMode = false,
                typedAnswer = "",
                selectedGrade = null,
                suggestedGrade = null,
                gradesLocked = false,
            )
        }
    }

    private fun enableTypingMode() {
        if (state.value.phase != PracticePhase.SHOW_FRONT) return
        setState {
            copy(
                isTypingMode = true,
                typedAnswer = "",
                selectedGrade = null,
                suggestedGrade = null,
                gradesLocked = false,
            )
        }
    }

    private fun evaluateTypedAnswer() {
        val card = state.value.currentCard ?: return
        if (state.value.phase != PracticePhase.SHOW_FRONT || !state.value.isTypingMode) return
        if (state.value.typedAnswer.isBlank()) return

        val typedAnswer = state.value.typedAnswer.trim()
        val continuation = card.expectedContinuation
        val diff = TextDiffHighlighter.diffUserWords(continuation, typedAnswer)
        val suggested = TextDiffHighlighter.suggestGradeFromChars(continuation, typedAnswer)
        setState {
            copy(
                phase = PracticePhase.FEEDBACK,
                diffTokens = diff,
                suggestedGrade = suggested,
                selectedGrade = suggested,
                gradesLocked = true,
            )
        }
    }

    private fun selectGrade(grade: SrsGrade) {
        if (state.value.phase != PracticePhase.REVEALED || state.value.gradesLocked) return
        setState { copy(selectedGrade = grade) }
    }

    private fun submitAndAdvance() {
        val cardId = state.value.currentCard?.id ?: return
        val grade = state.value.selectedGrade ?: return

        when (state.value.phase) {
            PracticePhase.REVEALED,
            PracticePhase.FEEDBACK,
            -> Unit
            else -> return
        }

        viewModelScope.launch {
            memorizationRepository
                .submitReview(cardId, grade)
                .onSuccess {
                    recordSessionStats(grade)
                    advanceToNextCard()
                }.onFailure { error ->
                    setState {
                        copy(screenState = UiScreenState.Error(message = error.toMemorizationUiText()))
                    }
                }
        }
    }

    private fun recordSessionStats(grade: SrsGrade) {
        setState {
            copy(
                sessionReviewed = sessionReviewed + 1,
                sessionMistakes =
                    sessionMistakes +
                        when (grade) {
                            SrsGrade.AGAIN, SrsGrade.HARD -> 1
                            SrsGrade.GOOD, SrsGrade.EASY -> 0
                        },
                sessionLearned =
                    sessionLearned +
                        when (grade) {
                            SrsGrade.GOOD, SrsGrade.EASY -> 1
                            SrsGrade.AGAIN, SrsGrade.HARD -> 0
                        },
            )
        }
    }

    private fun advanceToNextCard() {
        currentQueueIndex += 1
        showCardAt(currentQueueIndex)
    }
}

private fun MemorizationError.toMemorizationUiText(): UiText =
    when (this) {
        MemorizationError.MaxActivePoemsReached,
        MemorizationError.PoemNotFound,
        MemorizationError.CardNotFound,
        MemorizationError.Unknown,
        -> UiText.Resource(Res.string.error_unknown)
    }
