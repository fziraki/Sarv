package abkabk.azbarkon.core.widget

import abkabk.azbarkon.core.util.currentLocalDateSeed
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import org.junit.jupiter.api.Test

class RandomDistichWidgetSeedTest {
    @Test
    fun `dailyDistichSeed for all poets matches notification seed`() {
        assertThat(dailyDistichSeed(RandomDistichWidgetConstants.ALL_POETS_ID))
            .isEqualTo(currentLocalDateSeed())
    }

    @Test
    fun `dailyDistichSeed differs per poet on same day`() {
        val allPoetsSeed = dailyDistichSeed(RandomDistichWidgetConstants.ALL_POETS_ID)
        val poetSeed = dailyDistichSeed(7)

        assertThat(poetSeed).isNotEqualTo(allPoetsSeed)
    }

    @Test
    fun `randomDistichSeed differs from dailyDistichSeed`() {
        val appWidgetId = 42
        val dailySeed = dailyDistichSeed(RandomDistichWidgetConstants.ALL_POETS_ID)
        val randomSeed = randomDistichSeed(appWidgetId)

        assertThat(randomSeed).isNotEqualTo(dailySeed)
    }

    @Test
    fun `randomDistichSeed produces different values on consecutive calls`() {
        val appWidgetId = 42
        val firstSeed = randomDistichSeed(appWidgetId)
        Thread.sleep(1)
        val secondSeed = randomDistichSeed(appWidgetId)

        assertThat(secondSeed).isNotEqualTo(firstSeed)
    }
}
