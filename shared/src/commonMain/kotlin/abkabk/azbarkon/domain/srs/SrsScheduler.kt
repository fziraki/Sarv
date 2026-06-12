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
                SrsGrade.AGAIN -> clampEase(previousEase - 0.20)
                SrsGrade.HARD -> clampEase(previousEase - 0.15)
                SrsGrade.GOOD -> previousEase
                SrsGrade.EASY -> clampEase(previousEase + 0.15)
            }

        val newInterval =
            when (grade) {
                SrsGrade.AGAIN -> 1
                SrsGrade.HARD -> max(1, (previousInterval * 1.2).roundToInt())
                SrsGrade.GOOD -> max(1, (previousInterval * newEase).roundToInt())
                SrsGrade.EASY -> max(1, (previousInterval * newEase * 1.3).roundToInt())
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
            interval <= 3 -> 2
            interval <= 7 -> 3
            interval <= 14 -> 4
            else -> 5
        }

    fun clampEase(ease: Double): Double = ease.coerceIn(MIN_EASE, MAX_EASE)
}
