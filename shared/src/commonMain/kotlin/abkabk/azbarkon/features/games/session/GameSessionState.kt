package abkabk.azbarkon.features.games.session

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.domain.model.games.GameConstants
import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.domain.model.games.GameType
import androidx.compose.runtime.Stable

enum class QuizAnswerPhase {
    Answering,
    Correct,
    Wrong,
}

@Stable
data class GameSessionState(
    val screenState: UiScreenState = UiScreenState.Loading,
    val gameType: GameType = GameType.NEXT_VERSE,
    val coinBalance: Int = 0,
    val currentQuizIndex: Int = 0,
    val questions: List<GameQuestion> = emptyList(),
    val selectedOptionIndex: Int? = null,
    val selectedPoetId: Int? = null,
    val filledWords: List<String> = emptyList(),
    val orderedLineIds: List<String> = emptyList(),
    val initialOrderedLineIds: List<String> = emptyList(),
    val pinnedLineId: String? = null,
    val pinnedLineIndex: Int? = null,
    val disabledOptionIndices: Set<Int> = emptySet(),
    val answerPhase: QuizAnswerPhase = QuizAnswerPhase.Answering,
    val hintUsedThisQuiz: Boolean = false,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val noAnswerCount: Int = 0,
    val sessionScoreDelta: Int = 0,
) {
    val currentQuestion: GameQuestion?
        get() = questions.getOrNull(currentQuizIndex)

    val isLastQuiz: Boolean
        get() = currentQuizIndex >= GameConstants.QUIZ_COUNT - 1

    val canUseHint: Boolean
        get() =
            answerPhase == QuizAnswerPhase.Answering &&
                !hintUsedThisQuiz &&
                coinBalance >= GameConstants.HINT_COST

    val isAnswering: Boolean
        get() = answerPhase == QuizAnswerPhase.Answering

    val isRevealing: Boolean
        get() = currentQuestion != null && !isAnswering

    val hasSelection: Boolean
        get() =
            when (val question = currentQuestion) {
                is GameQuestion.NextVerse -> selectedOptionIndex != null
                is GameQuestion.FindPoet -> selectedPoetId != null
                is GameQuestion.CompletePoem -> filledWords.isNotEmpty()
                is GameQuestion.OrganizePoem ->
                    orderedLineIds.isNotEmpty() && orderedLineIds != initialOrderedLineIds
                null -> false
            }

    val canPressPrimaryAction: Boolean
        get() = currentQuestion != null && (isAnswering || isRevealing)
}
