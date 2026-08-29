package abkabk.azbarkon.domain.srs

import abkabk.azbarkon.domain.model.memorization.SrsCard
import abkabk.azbarkon.domain.model.memorization.SrsGrade
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class SrsSchedulerTest {
    private val baseCard =
        SrsCard(
            id = 1,
            poemId = 10,
            cardIndex = 0,
            front = "front",
            back = "back",
            interval = 2,
            ease = 2.5,
            dueDateMillis = 0,
            consecutiveCorrect = 1,
        )

    @Test
    fun `again resets interval and decreases ease`() {
        val result = SrsScheduler.applyReview(baseCard, SrsGrade.AGAIN, nowMillis = 1_000L)

        assertThat(result.interval).isEqualTo(1)
        assertThat(result.ease).isEqualTo(2.3)
        assertThat(result.consecutiveCorrect).isEqualTo(0)
        assertThat(result.dueDateMillis).isEqualTo(1_000L + SrsScheduler.MILLIS_PER_DAY)
    }

    @Test
    fun `good multiplies interval by ease`() {
        val result = SrsScheduler.applyReview(baseCard, SrsGrade.GOOD, nowMillis = 0)

        assertThat(result.interval).isEqualTo(5)
        assertThat(result.ease).isEqualTo(2.5)
        assertThat(result.consecutiveCorrect).isEqualTo(2)
    }

    @Test
    fun `easy increases ease and interval more`() {
        val result = SrsScheduler.applyReview(baseCard, SrsGrade.EASY, nowMillis = 0)

        assertThat(result.interval).isEqualTo(7)
        assertThat(result.ease).isEqualTo(2.65)
    }

    @Test
    fun `ease is clamped to minimum`() {
        val lowEaseCard = baseCard.copy(ease = 1.35)
        val result = SrsScheduler.applyReview(lowEaseCard, SrsGrade.AGAIN, nowMillis = 0)

        assertThat(result.ease).isEqualTo(SrsScheduler.MIN_EASE)
    }

    @Test
    fun `box level derives from interval`() {
        assertThat(SrsScheduler.boxFromInterval(1)).isEqualTo(1)
        assertThat(SrsScheduler.boxFromInterval(3)).isEqualTo(2)
        assertThat(SrsScheduler.boxFromInterval(10)).isEqualTo(4)
    }
}
