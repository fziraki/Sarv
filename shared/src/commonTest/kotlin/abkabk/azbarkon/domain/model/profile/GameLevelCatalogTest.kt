package abkabk.azbarkon.domain.model.profile

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class GameLevelCatalogTest {
    @Test
    fun `requiredScoreForLevel returns zero for level one`() {
        assertThat(GameLevelCatalog.requiredScoreForLevel(1)).isEqualTo(0)
    }

    @Test
    fun `requiredScoreForLevel scales by XP_PER_LEVEL`() {
        assertThat(GameLevelCatalog.requiredScoreForLevel(3)).isEqualTo(1800)
        assertThat(GameLevelCatalog.requiredScoreForLevel(8)).isEqualTo(6300)
    }

    @Test
    fun `progressFromCoinBalance maps coin balance to level and remainder`() {
        val progress = GameLevelCatalog.progressFromCoinBalance(coinBalance = 1799)

        assertThat(progress.levelId).isEqualTo(2)
        assertThat(progress.currentXp).isEqualTo(899)
        assertThat(progress.targetXp).isEqualTo(GameLevelCatalog.XP_PER_LEVEL)
    }
}
