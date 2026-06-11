package abkabk.azbarkon.domain.datasource

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.memorization.SrsCard
import abkabk.azbarkon.domain.model.memorization.SrsGrade

interface MemorizationLocalDataSource {
    suspend fun countActivePoems(): Int

    suspend fun isPoemActive(poemId: Int): Boolean

    suspend fun insertActivePoem(
        poemId: Int,
        addedAtMillis: Long,
        status: String,
    )

    suspend fun deleteActivePoem(poemId: Int)

    suspend fun getActivePoemIds(): List<Int>

    suspend fun getActivePoemAddedAt(poemId: Int): Long?

    suspend fun insertCards(cards: List<SrsCard>)

    suspend fun getCardById(cardId: Long): SrsCard?

    suspend fun getDueCards(
        nowMillis: Long,
        poemId: Int? = null,
    ): List<SrsCard>

    suspend fun countDueCards(
        nowMillis: Long,
        poemId: Int? = null,
    ): Int

    suspend fun updateCard(card: SrsCard)

    suspend fun countCardsByPoemId(poemId: Int): Int

    suspend fun countReviewedCardsByPoemId(poemId: Int): Int

    suspend fun getAverageInterval(poemId: Int): Int

    suspend fun getMaxConsecutiveCorrect(poemId: Int): Int

    suspend fun insertReviewLog(
        cardId: Long,
        grade: SrsGrade,
        previousInterval: Int,
        newInterval: Int,
        reviewTimeMillis: Long,
    )

    suspend fun findPoetIdByName(nameFragment: String): Result<Int, DataError.Local>

    suspend fun findCategoryByPoetAndText(
        poetId: Int,
        textFragment: String,
    ): Result<Pair<Int, String>, DataError.Local>
}
