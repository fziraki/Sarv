package abkabk.azbarkon.domain.repository

import abkabk.azbarkon.core.domain.result.EmptyResult
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.memorization.ActiveMemorizationPoem
import abkabk.azbarkon.domain.model.memorization.MemorizationError
import abkabk.azbarkon.domain.model.memorization.MemorizationSummary
import abkabk.azbarkon.domain.model.memorization.QuickStartTarget
import abkabk.azbarkon.domain.model.memorization.SrsCard
import abkabk.azbarkon.domain.model.memorization.SrsGrade
import kotlinx.coroutines.flow.Flow

interface MemorizationRepository {
    fun observeActiveSummary(): Flow<MemorizationSummary>

    suspend fun getActivePoems(): Result<List<ActiveMemorizationPoem>, MemorizationError>

    suspend fun addPoem(poemId: Int): EmptyResult<MemorizationError>

    suspend fun removePoem(poemId: Int): EmptyResult<MemorizationError>

    suspend fun getDueCards(poemId: Int? = null): Result<List<SrsCard>, MemorizationError>

    suspend fun submitReview(
        cardId: Long,
        grade: SrsGrade,
    ): Result<SrsCard, MemorizationError>

    suspend fun isPoemActive(poemId: Int): Boolean

    suspend fun resolveQuickStart(
        poetNameFragment: String,
        categoryTextFragment: String? = null,
    ): QuickStartTarget
}
