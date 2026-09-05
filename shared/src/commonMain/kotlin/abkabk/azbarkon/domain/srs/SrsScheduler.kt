package abkabk.azbarkon.domain.srs

import abkabk.azbarkon.core.util.currentTimeMillis
import abkabk.azbarkon.domain.model.memorization.SrsGrade

object SrsScheduler {
    const val MILLIS_PER_DAY = 86_400_000L

    // Grade score deltas
    private const val AGAIN_DELTA = -1.20
    private const val HARD_DELTA = -1.15
    private const val GOOD_DELTA = 0.0
    private const val EASY_DELTA = 1.15

    data class ReviewResult(
        val interval: Int,
        val score: Double,
        val dueDateMillis: Long,
        val consecutiveEasy: Int,
    )

    fun updateVerseScore(currentScore: Double, grade: SrsGrade): Double =
        when (grade) {
            SrsGrade.AGAIN -> currentScore + AGAIN_DELTA
            SrsGrade.HARD -> currentScore + HARD_DELTA
            SrsGrade.GOOD -> currentScore + GOOD_DELTA
            SrsGrade.EASY -> currentScore + EASY_DELTA
        }

    fun calculatePoemInterval(
        verseScores: List<Double>,
        consecutiveEasy: Int,
    ): ReviewResult {
        val total = verseScores.sum()
        val minTotal = verseScores.size * 1.0

        val interval: Int
        val newConsecutiveEasy: Int

        when {
            total < minTotal -> {
                interval = 1
                newConsecutiveEasy = 0
            }
            total == minTotal -> {
                interval = 2
                newConsecutiveEasy = 0
            }
            else -> {
                newConsecutiveEasy = consecutiveEasy + 1
                interval = newConsecutiveEasy
            }
        }

        return ReviewResult(
            interval = interval,
            score = verseScores.average(),
            dueDateMillis = currentTimeMillis() + interval * MILLIS_PER_DAY,
            consecutiveEasy = newConsecutiveEasy,
        )
    }

    private const val BOX_1_MAX = 1
    private const val BOX_2_MAX = 3
    private const val BOX_3_MAX = 7
    private const val BOX_4_MAX = 14

    private const val BOX_LEVEL_3 = 3
    private const val BOX_LEVEL_4 = 4
    private const val BOX_LEVEL_5 = 5

    fun boxFromInterval(interval: Int): Int =
        when {
            interval <= BOX_1_MAX -> 1
            interval <= BOX_2_MAX -> 2
            interval <= BOX_3_MAX -> BOX_LEVEL_3
            interval <= BOX_4_MAX -> BOX_LEVEL_4
            else -> BOX_LEVEL_5
        }
}
