package abkabk.azbarkon.domain.srs

import abkabk.azbarkon.core.util.currentTimeMillis
import abkabk.azbarkon.domain.model.memorization.SrsCard
import abkabk.azbarkon.domain.model.memorization.SrsGrade
import kotlin.math.max
import kotlin.math.roundToInt

object SrsScheduler {
    const val MIN_EASE = 1.3
    const val MAX_EASE = 2.8
    const val DEFAULT_EASE = 2.5
    const val MILLIS_PER_DAY = 86_400_000L

    private const val AGAIN_EASE_PENALTY = 0.20
    private const val HARD_EASE_PENALTY = 0.15
    private const val EASY_EASE_BONUS = 0.15
    private const val HARD_INTERVAL_FACTOR = 1.2
    private const val EASY_INTERVAL_FACTOR = 1.3
    private const val BOX_2_UP_TO_DAYS = 3
    private const val BOX_3_UP_TO_DAYS = 7
    private const val BOX_4_UP_TO_DAYS = 14
    private const val BOX_3 = 3
    private const val BOX_4 = 4
    private const val MAX_BOX_NUMBER = 5

    data class ReviewResult(
        val interval: Int,
        val ease: Double,
        val dueDateMillis: Long,
        val consecutiveCorrect: Int,
    )

    fun applyReview(
        card: SrsCard,
        grade: SrsGrade,
        nowMillis: Long = currentTimeMillis(),
    ): ReviewResult {
        val previousInterval = card.interval
        val previousEase = card.ease

        val newEase =
            when (grade) {
                SrsGrade.AGAIN -> clampEase(previousEase - AGAIN_EASE_PENALTY)
                SrsGrade.HARD -> clampEase(previousEase - HARD_EASE_PENALTY)
                SrsGrade.GOOD -> previousEase
                SrsGrade.EASY -> clampEase(previousEase + EASY_EASE_BONUS)
            }

        val newInterval =
            when (grade) {
                SrsGrade.AGAIN -> 1
                SrsGrade.HARD -> max(1, (previousInterval * HARD_INTERVAL_FACTOR).roundToInt())
                SrsGrade.GOOD -> max(1, (previousInterval * newEase).roundToInt())
                SrsGrade.EASY -> max(1, (previousInterval * newEase * EASY_INTERVAL_FACTOR).roundToInt())
            }

        val consecutiveCorrect =
            when (grade) {
                SrsGrade.AGAIN -> 0
                else -> card.consecutiveCorrect + 1
            }

        return ReviewResult(
            interval = newInterval,
            ease = newEase,
            dueDateMillis = nowMillis + newInterval * MILLIS_PER_DAY,
            consecutiveCorrect = consecutiveCorrect,
        )
    }

    fun boxFromInterval(interval: Int): Int =
        when {
            interval <= 1 -> 1
            interval <= BOX_2_UP_TO_DAYS -> 2
            interval <= BOX_3_UP_TO_DAYS -> BOX_3
            interval <= BOX_4_UP_TO_DAYS -> BOX_4
            else -> MAX_BOX_NUMBER
        }

    fun clampEase(ease: Double): Double = ease.coerceIn(MIN_EASE, MAX_EASE)
}
