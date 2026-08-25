package abkabk.azbarkon.domain.usecase

import abkabk.azbarkon.domain.model.games.GameQuestion

class EvaluateGameAnswerUseCase {
    operator fun invoke(
        question: GameQuestion?,
        selectedOptionIndex: Int?,
        selectedPoetId: Int?,
        filledWords: List<String>,
        orderedLineIds: List<String>,
    ): Boolean {
        return when (question) {
            is GameQuestion.NextVerse -> selectedOptionIndex == question.correctIndex
            is GameQuestion.FindPoet -> selectedPoetId == question.correctPoetId
            is GameQuestion.CompletePoem -> filledWords.size == 2 &&
                filledWords[0] == question.correctWords.first &&
                filledWords[1] == question.correctWords.second
            is GameQuestion.OrganizePoem -> orderedLineIds == question.correctOrder
            null -> false
        }
    }
}
