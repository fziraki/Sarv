package abkabk.azbarkon.features.games.session

import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.uidata.BaseViewModel
import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.core.uidata.UiText
import abkabk.azbarkon.domain.model.games.GameConstants
import abkabk.azbarkon.domain.model.games.GameGenerationCache
import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.domain.model.games.GameSessionSummary
import abkabk.azbarkon.domain.model.games.GameType
import abkabk.azbarkon.domain.repository.GamesRepository
import abkabk.azbarkon.domain.repository.UserPreferencesRepository
import abkabk.azbarkon.domain.usecase.ApplyGameHintUseCase
import abkabk.azbarkon.domain.usecase.EvaluateGameAnswerUseCase
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.error_unknown
import androidx.lifecycle.viewModelScope
import azbarkoncmp.shared.generated.resources.game_hint_not_enough_score
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameSessionViewModel(
    private val gameType: GameType,
    private val gamesRepository: GamesRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val evaluateGameAnswer: EvaluateGameAnswerUseCase,
    private val applyGameHint: ApplyGameHintUseCase,
) : BaseViewModel<GameSessionAction, GameSessionState, GameSessionEvent>(
        initialState = GameSessionState(gameType = gameType),
    ) {
    private var prefetchJob: Job? = null
    private val sessionSeed = Random.nextLong()

    init {
        loadSession()
    }

    override fun onAction(action: GameSessionAction) {
        when (action) {
            GameSessionAction.OnBackClick -> {
                cancelJobs()
                viewModelScope.launch { sendEvent(GameSessionEvent.NavigateBack) }
            }

            GameSessionAction.OnHintClick -> applyHint()

            GameSessionAction.OnCheckAnswerClick -> onPrimaryActionClick()

            is GameSessionAction.OnOptionSelected -> {
                if (state.value.isAnswering) {
                    setState { copy(selectedOptionIndex = action.index) }
                }
            }

            is GameSessionAction.OnPoetSelected -> {
                if (state.value.isAnswering) {
                    setState { copy(selectedPoetId = action.poetId) }
                }
            }

            is GameSessionAction.OnWordSelected -> {
                val question = state.value.currentQuestion as? GameQuestion.CompletePoem ?: return
                if (state.value.isAnswering && action.word in question.options) {
                    val current = state.value.filledWords
                    setState {
                        copy(
                            filledWords =
                                when {
                                    action.word in current -> current - action.word
                                    current.size < 2 -> current + action.word
                                    else -> current
                                },
                        )
                    }
                }
            }

            is GameSessionAction.OnReorderLines -> {
                val current = state.value.orderedLineIds.toMutableList()
                val fromIndex = action.fromIndex
                val toIndex = action.toIndex
                val pinnedId = state.value.pinnedLineId
                if (!state.value.isAnswering ||
                    fromIndex !in current.indices ||
                    toIndex !in current.indices
                ) {
                    return
                }
                if (pinnedId != null) {
                    val pinnedIndex = state.value.pinnedLineIndex
                    if (pinnedIndex == null || fromIndex == pinnedIndex || toIndex == pinnedIndex) {
                        return
                    }
                    val dragged = current[fromIndex]
                    current[fromIndex] = current[toIndex]
                    current[toIndex] = dragged
                } else {
                    val item = current.removeAt(fromIndex)
                    current.add(toIndex, item)
                }
                setState { copy(orderedLineIds = current) }
            }
        }
    }

    private fun loadSession() {
        cancelJobs()
        prefetchJob?.cancel()
        viewModelScope.launch {
            setState {
                GameSessionState(
                    gameType = gameType,
                    screenState = UiScreenState.Loading,
                )
            }
            val coinBalance = userPreferencesRepository.getCoinBalance()
            val cache = gamesRepository.createGenerationCache()
            when (
                val firstQuestion =
                    gamesRepository.generateQuestion(
                        gameType = gameType,
                        sessionSeed = sessionSeed,
                        quizIndex = 0,
                        cache = cache,
                    )
            ) {
                is Result.Success -> {
                    val questions = mutableListOf(firstQuestion.data)
                    setState {
                        copy(
                            screenState = UiScreenState.Success,
                            coinBalance = coinBalance,
                            questions = questions.toList(),
                            currentQuizIndex = 0,
                        )
                    }
                    resetQuizState()
                    prefetchRemainingQuestions(cache, questions)
                }

                is Result.Error -> {
                    setState {
                        copy(
                            screenState =
                                UiScreenState.Error(
                                    message = UiText.Resource(Res.string.error_unknown),
                                ),
                        )
                    }
                }
            }
        }
    }

    private fun prefetchRemainingQuestions(
        cache: GameGenerationCache,
        questions: MutableList<GameQuestion>,
    ) {
        prefetchJob =
            viewModelScope.launch {
                for (quizIndex in 1 until GameConstants.QUIZ_COUNT) {
                    when (
                        val result =
                            gamesRepository.generateQuestion(
                                gameType = gameType,
                                sessionSeed = sessionSeed,
                                quizIndex = quizIndex,
                                cache = cache,
                            )
                    ) {
                        is Result.Success -> {
                            questions += result.data
                            setState { copy(questions = questions.toList()) }
                        }

                        is Result.Error -> return@launch
                    }
                }
            }
    }

    private suspend fun waitForQuestion(index: Int) {
        while (state.value.questions.size <= index) {
            val job = prefetchJob
            if (job == null || !job.isActive) break
            delay(PREFETCH_POLL_MS)
        }
    }

    private fun resetQuizState() {
        val question = state.value.currentQuestion
        val organizeInitialOrder =
            when (question) {
                is GameQuestion.OrganizePoem -> question.lines.map { it.id }
                else -> emptyList()
            }
        setState {
            copy(
                selectedOptionIndex = null,
                selectedPoetId = null,
                filledWords = emptyList(),
                orderedLineIds = organizeInitialOrder,
                initialOrderedLineIds = organizeInitialOrder,
                pinnedLineId = null,
                pinnedLineIndex = null,
                disabledOptionIndices = emptySet(),
                answerPhase = QuizAnswerPhase.Answering,
                hintUsedThisQuiz = false,
            )
        }
    }

    private fun onPrimaryActionClick() {
        if (state.value.currentQuestion == null) return
        when {
            state.value.isRevealing -> advanceQuiz()
            state.value.isAnswering ->
                if (state.value.hasSelection) {
                    checkAnswer()
                } else {
                    applySkipOutcome()
                }
        }
    }

    private fun checkAnswer() {
        val isCorrect = evaluateAnswer()
        val phase = if (isCorrect) QuizAnswerPhase.Correct else QuizAnswerPhase.Wrong
        applyOutcome(isCorrect = isCorrect, phase = phase)
    }

    private fun evaluateAnswer(): Boolean {
        val currentState = state.value
        return evaluateGameAnswer(
            question = currentState.currentQuestion,
            selectedOptionIndex = currentState.selectedOptionIndex,
            selectedPoetId = currentState.selectedPoetId,
            filledWords = currentState.filledWords,
            orderedLineIds = currentState.orderedLineIds,
        )
    }

    private fun applySkipOutcome() {
        val question = state.value.currentQuestion
        setState {
            copy(
                answerPhase = QuizAnswerPhase.Wrong,
                noAnswerCount = noAnswerCount + 1,
                orderedLineIds =
                    when (question) {
                        is GameQuestion.OrganizePoem -> question.correctOrder
                        else -> orderedLineIds
                    },
            )
        }
    }

    private fun applyOutcome(
        isCorrect: Boolean,
        phase: QuizAnswerPhase,
    ) {
        val scoreDelta = if (isCorrect) gameType.baseScore else 0
        val updatedCoinBalance = userPreferencesRepository.adjustCoinBalance(scoreDelta)

        setState {
            copy(
                answerPhase = phase,
                sessionScoreDelta = sessionScoreDelta + scoreDelta,
                coinBalance = updatedCoinBalance,
                correctCount = if (isCorrect) correctCount + 1 else correctCount,
                wrongCount = if (isCorrect) wrongCount else wrongCount + 1,
            )
        }
    }

    private fun advanceQuiz() {
        viewModelScope.launch {
            if (state.value.isLastQuiz) {
                finishSession()
                return@launch
            }
            val nextIndex = state.value.currentQuizIndex + 1
            waitForQuestion(nextIndex)
            if (state.value.questions.getOrNull(nextIndex) == null) {
                setState {
                    copy(
                        screenState =
                            UiScreenState.Error(
                                message = UiText.Resource(Res.string.error_unknown),
                            ),
                    )
                }
                return@launch
            }
            setState { copy(currentQuizIndex = nextIndex) }
            resetQuizState()
        }
    }

    private suspend fun finishSession() {
        cancelJobs()
        val currentState = state.value
        val isPerfect =
            currentState.wrongCount == 0 &&
                currentState.noAnswerCount == 0 &&
                currentState.correctCount == GameConstants.QUIZ_COUNT
        userPreferencesRepository.recordCompletedSession(
            correct = currentState.correctCount,
            wrong = currentState.wrongCount,
            playedAtMillis = abkabk.azbarkon.core.util.currentTimeMillis(),
            isPerfect = isPerfect,
        )
        sendEvent(
            GameSessionEvent.NavigateToResult(
                gameType = gameType,
                summary =
                    GameSessionSummary(
                        correctCount = currentState.correctCount,
                        wrongCount = currentState.wrongCount,
                        noAnswerCount = currentState.noAnswerCount,
                        scoreDelta = currentState.sessionScoreDelta,
                    ),
            ),
        )
    }

    private fun applyHint() {
        if (!state.value.canUseHint) {
            viewModelScope.launch {
                sendEvent(
                    GameSessionEvent.ShowSnackbar(
                        UiText.Resource(Res.string.game_hint_not_enough_score)
                    )
                )
            }
            return
        }
        val question = state.value.currentQuestion ?: return
        
        val currentState = state.value
        val hintResult = applyGameHint(
            question = question,
            currentState = ApplyGameHintUseCase.HintResult(
                disabledOptionIndices = currentState.disabledOptionIndices,
                selectedOptionIndex = currentState.selectedOptionIndex,
                selectedPoetId = currentState.selectedPoetId,
                orderedLineIds = currentState.orderedLineIds,
                pinnedLineId = currentState.pinnedLineId,
                pinnedLineIndex = currentState.pinnedLineIndex,
                coinBalance = currentState.coinBalance,
            )
        ) ?: return

        setState {
            copy(
                coinBalance = hintResult.coinBalance,
                hintUsedThisQuiz = true,
                disabledOptionIndices = hintResult.disabledOptionIndices,
                selectedOptionIndex = hintResult.selectedOptionIndex,
                selectedPoetId = hintResult.selectedPoetId,
                orderedLineIds = hintResult.orderedLineIds,
                pinnedLineId = hintResult.pinnedLineId,
                pinnedLineIndex = hintResult.pinnedLineIndex,
            )
        }
    }

    private fun cancelJobs() {
        prefetchJob?.cancel()
    }

    override fun onCleared() {
        cancelJobs()
        super.onCleared()
    }

    private companion object {
        const val PREFETCH_POLL_MS = 50L
    }
}
