package abkabk.azbarkon.domain.usecase

import abkabk.azbarkon.domain.model.games.GameConstants
import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.domain.repository.UserPreferencesRepository

class ApplyGameHintUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    data class HintResult(
        val disabledOptionIndices: Set<Int>,
        val selectedOptionIndex: Int?,
        val selectedPoetId: Int?,
        val orderedLineIds: List<String>,
        val pinnedLineId: String?,
        val pinnedLineIndex: Int?,
        val coinBalance: Int,
    )

    operator fun invoke(
        question: GameQuestion?,
        currentState: HintResult,
    ): HintResult? {
        if (question == null) return null

        return when (question) {
            is GameQuestion.NextVerse -> applyNextVerseHint(question, currentState)
            is GameQuestion.FindPoet -> applyFindPoetHint(question, currentState)
            is GameQuestion.CompletePoem -> applyCompletePoemHint(question, currentState)
            is GameQuestion.OrganizePoem -> applyOrganizePoemHint(question, currentState)
        }
    }

    private fun applyNextVerseHint(
        question: GameQuestion.NextVerse,
        currentState: HintResult,
    ): HintResult {
        val toDisable = question.options.indices
            .filter { it != question.correctIndex && it !in currentState.disabledOptionIndices }
            .firstOrNull()
            ?: return currentState

        val updatedBalance = userPreferencesRepository.adjustCoinBalance(-GameConstants.HINT_COST)
        val clearedSelection = if (currentState.selectedOptionIndex == toDisable) null else currentState.selectedOptionIndex

        return currentState.copy(
            coinBalance = updatedBalance,
            disabledOptionIndices = currentState.disabledOptionIndices + toDisable,
            selectedOptionIndex = clearedSelection,
        )
    }

    private fun applyFindPoetHint(
        question: GameQuestion.FindPoet,
        currentState: HintResult,
    ): HintResult {
        val wrongPoetIds = question.options.map { it.id }.filter { it != question.correctPoetId }
        val toDisable = question.options.indices
            .filter { question.options[it].id in wrongPoetIds && it !in currentState.disabledOptionIndices }
            .firstOrNull()
            ?: return currentState

        val disabledPoetId = question.options[toDisable].id
        val updatedBalance = userPreferencesRepository.adjustCoinBalance(-GameConstants.HINT_COST)
        val clearedSelection = if (currentState.selectedPoetId == disabledPoetId) null else currentState.selectedPoetId

        return currentState.copy(
            coinBalance = updatedBalance,
            disabledOptionIndices = currentState.disabledOptionIndices + toDisable,
            selectedPoetId = clearedSelection,
        )
    }

    private fun applyCompletePoemHint(
        question: GameQuestion.CompletePoem,
        currentState: HintResult,
    ): HintResult {
        val wrongWords = question.options.filter {
            it != question.correctWords.first && it != question.correctWords.second
        }
        val toDisableIndex = question.options.indices.firstOrNull { index ->
            question.options[index] in wrongWords && index !in currentState.disabledOptionIndices
        } ?: return currentState

        val updatedBalance = userPreferencesRepository.adjustCoinBalance(-GameConstants.HINT_COST)

        return currentState.copy(
            coinBalance = updatedBalance,
            disabledOptionIndices = currentState.disabledOptionIndices + toDisableIndex,
        )
    }

    private fun applyOrganizePoemHint(
        question: GameQuestion.OrganizePoem,
        currentState: HintResult,
    ): HintResult {
        val currentOrder = currentState.orderedLineIds
        val wrongIndex = currentOrder.indices.firstOrNull { index ->
            currentOrder[index] != question.correctOrder[index]
        } ?: return currentState

        val lineId = currentOrder[wrongIndex]
        val targetIndex = question.correctOrder.indexOf(lineId)
        if (targetIndex < 0) return currentState

        val reordered = currentOrder.toMutableList()
        reordered.removeAt(wrongIndex)
        reordered.add(targetIndex, lineId)

        val updatedBalance = userPreferencesRepository.adjustCoinBalance(-GameConstants.HINT_COST)

        return currentState.copy(
            coinBalance = updatedBalance,
            orderedLineIds = reordered,
            pinnedLineId = lineId,
            pinnedLineIndex = targetIndex,
        )
    }
}
