package abkabk.azbarkon.features.games.session

import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.ui_base.BaseViewModel
import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.domain.model.games.GameGenerationCache
import abkabk.azbarkon.domain.model.games.GameConstants
import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.domain.model.games.GameSessionSummary
import abkabk.azbarkon.domain.model.games.GameType
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.error_unknown
import abkabk.azbarkon.domain.model.games.baseScore
import abkabk.azbarkon.domain.repository.GamesRepository
import abkabk.azbarkon.domain.repository.UserPreferencesRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameSessionViewModel(
    private val gameType: GameType,
    private val gamesRepository: GamesRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<GameSessionAction, GameSessionState, GameSessionEvent>(
        initialState = GameSessionState(gameType = gameType),
    ) {
    private var timerJob: Job? = null
    private var revealJob: Job? = null
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

            GameSessionAction.OnCheckAnswerClick -> checkAnswer()

            GameSessionAction.OnRetryClick -> loadSession()

            is GameSessionAction.OnOptionSelected -> {
                if (!state.value.isAnswering) return
                setState { copy(selectedOptionIndex = action.index) }
            }

            is GameSessionAction.OnPoetSelected -> {
                if (!state.value.isAnswering) return
                setState { copy(selectedPoetId = action.poetId) }
            }

            is GameSessionAction.OnWordSelected -> {
                if (!state.value.isAnswering) return
                val question = state.value.currentQuestion as? GameQuestion.CompletePoem ?: return
                if (state.value.filledWords.size >= 2) return
                if (action.word !in question.options) return
                if (action.word in state.value.filledWords) return
                setState { copy(filledWords = filledWords + action.word) }
            }

            is GameSessionAction.OnReorderLines -> {
                if (!state.value.isAnswering) return
                val pinnedId = state.value.pinnedLineId
                val current = state.value.orderedLineIds.toMutableList()
                if (action.fromIndex !in current.indices || action.toIndex !in current.indices) return
                if (current[action.fromIndex] == pinnedId || current[action.toIndex] == pinnedId) return
                val item = current.removeAt(action.fromIndex)
                current.add(action.toIndex, item)
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
                    startTimer()
                    prefetchRemainingQuestions(cache, questions)
                }

                is Result.Error -> {
                    setState {
                        copy(
                            screenState =
                                UiScreenState.Error(
                                    message = UiText.Resource(Res.string.error_unknown),
                                    retryable = true,
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
        setState {
            copy(
                timeRemainingSeconds = GameConstants.TIME_LIMIT_SECONDS,
                selectedOptionIndex = null,
                selectedPoetId = null,
                filledWords = emptyList(),
                orderedLineIds =
                    when (question) {
                        is GameQuestion.OrganizePoem -> question.lines.map { it.id }
                        else -> emptyList()
                    },
                pinnedLineId = null,
                disabledOptionIndices = emptySet(),
                answerPhase = QuizAnswerPhase.Answering,
                hintUsedThisQuiz = false,
            )
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob =
            viewModelScope.launch {
                while (state.value.timeRemainingSeconds > 0 && state.value.isAnswering) {
                    delay(1_000)
                    if (!state.value.isAnswering) return@launch
                    val remaining = state.value.timeRemainingSeconds - 1
                    setState {
                        copy(
                            timeRemainingSeconds = remaining,
                            totalElapsedSeconds = totalElapsedSeconds + 1,
                        )
                    }
                    if (remaining == 0) {
                        handleTimeout()
                    }
                }
            }
    }

    private fun handleTimeout() {
        if (!state.value.isAnswering) return
        applyOutcome(isCorrect = false, phase = QuizAnswerPhase.Timeout)
    }

    private fun checkAnswer() {
        if (!state.value.canCheckAnswer) return
        val isCorrect = evaluateAnswer()
        val phase = if (isCorrect) QuizAnswerPhase.Correct else QuizAnswerPhase.Wrong
        applyOutcome(isCorrect = isCorrect, phase = phase)
    }

    private fun evaluateAnswer(): Boolean {
        val currentState = state.value
        return when (val question = currentState.currentQuestion) {
            is GameQuestion.NextVerse ->
                currentState.selectedOptionIndex == question.correctIndex

            is GameQuestion.FindPoet ->
                currentState.selectedPoetId == question.correctPoetId

            is GameQuestion.CompletePoem ->
                currentState.filledWords.size == 2 &&
                    currentState.filledWords[0] == question.correctWords.first &&
                    currentState.filledWords[1] == question.correctWords.second

            is GameQuestion.OrganizePoem ->
                currentState.orderedLineIds == question.correctOrder

            null -> false
        }
    }

    private fun applyOutcome(
        isCorrect: Boolean,
        phase: QuizAnswerPhase,
    ) {
        timerJob?.cancel()
        val baseScore = gameType.baseScore()
        val scoreDelta = if (isCorrect) baseScore else -baseScore
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

        revealJob?.cancel()
        revealJob =
            viewModelScope.launch {
                delay(REVEAL_DELAY_MS)
                advanceQuiz()
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
                                retryable = true,
                            ),
                    )
                }
                return@launch
            }
            setState { copy(currentQuizIndex = nextIndex) }
            resetQuizState()
            startTimer()
        }
    }

    private fun finishSession() {
        cancelJobs()
        val currentState = state.value
        viewModelScope.launch {
            sendEvent(
                GameSessionEvent.NavigateToResult(
                    gameType = gameType,
                    summary =
                        GameSessionSummary(
                            correctCount = currentState.correctCount,
                            wrongCount = currentState.wrongCount,
                            totalSeconds = currentState.totalElapsedSeconds,
                            scoreDelta = currentState.sessionScoreDelta,
                        ),
                ),
            )
        }
    }

    private fun applyHint() {
        if (!state.value.canUseHint) return
        val question = state.value.currentQuestion ?: return
        val currentState = state.value

        when (question) {
            is GameQuestion.NextVerse -> {
                val toDisable =
                    question.options.indices
                        .filter { it != question.correctIndex && it !in currentState.disabledOptionIndices }
                        .firstOrNull()
                        ?: return
                val updatedBalance =
                    userPreferencesRepository.adjustCoinBalance(-GameConstants.HINT_COST)
                val clearedSelection =
                    if (currentState.selectedOptionIndex == toDisable) null else currentState.selectedOptionIndex
                setState {
                    copy(
                        coinBalance = updatedBalance,
                        hintUsedThisQuiz = true,
                        disabledOptionIndices = disabledOptionIndices + toDisable,
                        selectedOptionIndex = clearedSelection,
                    )
                }
            }

            is GameQuestion.FindPoet -> {
                val wrongPoetIds =
                    question.options
                        .map { it.id }
                        .filter { it != question.correctPoetId }
                val toDisable =
                    question.options.indices
                        .filter { question.options[it].id in wrongPoetIds && it !in currentState.disabledOptionIndices }
                        .firstOrNull()
                        ?: return
                val disabledPoetId = question.options[toDisable].id
                val updatedBalance =
                    userPreferencesRepository.adjustCoinBalance(-GameConstants.HINT_COST)
                val clearedSelection =
                    if (currentState.selectedPoetId == disabledPoetId) null else currentState.selectedPoetId
                setState {
                    copy(
                        coinBalance = updatedBalance,
                        hintUsedThisQuiz = true,
                        disabledOptionIndices = disabledOptionIndices + toDisable,
                        selectedPoetId = clearedSelection,
                    )
                }
            }

            is GameQuestion.CompletePoem -> {
                val wrongWords =
                    question.options.filter {
                        it != question.correctWords.first && it != question.correctWords.second
                    }
                val toDisableIndex =
                    question.options.indices.firstOrNull { index ->
                        question.options[index] in wrongWords &&
                            index !in currentState.disabledOptionIndices
                    } ?: return
                val updatedBalance =
                    userPreferencesRepository.adjustCoinBalance(-GameConstants.HINT_COST)
                setState {
                    copy(
                        coinBalance = updatedBalance,
                        hintUsedThisQuiz = true,
                        disabledOptionIndices = disabledOptionIndices + toDisableIndex,
                    )
                }
            }

            is GameQuestion.OrganizePoem -> {
                val currentOrder = currentState.orderedLineIds
                val wrongIndex =
                    currentOrder.indices.firstOrNull { index ->
                        currentOrder[index] != question.correctOrder[index]
                    } ?: return
                val lineId = currentOrder[wrongIndex]
                val targetIndex = question.correctOrder.indexOf(lineId)
                if (targetIndex < 0) return
                val reordered = currentOrder.toMutableList()
                reordered.removeAt(wrongIndex)
                reordered.add(targetIndex, lineId)
                val updatedBalance =
                    userPreferencesRepository.adjustCoinBalance(-GameConstants.HINT_COST)
                setState {
                    copy(
                        coinBalance = updatedBalance,
                        hintUsedThisQuiz = true,
                        orderedLineIds = reordered,
                        pinnedLineId = lineId,
                    )
                }
            }
        }
    }

    private fun cancelJobs() {
        timerJob?.cancel()
        revealJob?.cancel()
        prefetchJob?.cancel()
    }

    override fun onCleared() {
        cancelJobs()
        super.onCleared()
    }

    private companion object {
        const val REVEAL_DELAY_MS = 1_500L
        const val PREFETCH_POLL_MS = 50L
    }
}
