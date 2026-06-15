package abkabk.azbarkon.domain.model.profile

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

class BadgeCatalogTest {
    @Test
    fun `first ghazal badge earned when any poem is fully reviewed`() {
        assertThat(
            BadgeCatalog.resolveEarned(
                badgeId = 1,
                hasCompletedMemorizationPoem = true,
                reviewedVersesCount = 0,
                gameVisitStreak = 0,
                perfectGameSessions = 0,
            ),
        ).isTrue()

        assertThat(
            BadgeCatalog.resolveEarned(
                badgeId = 1,
                hasCompletedMemorizationPoem = false,
                reviewedVersesCount = 200,
                gameVisitStreak = 10,
                perfectGameSessions = 10,
            ),
        ).isFalse()
    }

    @Test
    fun `hundred verses badge earned at 100 reviewed verses`() {
        assertThat(
            BadgeCatalog.resolveEarned(
                badgeId = 2,
                hasCompletedMemorizationPoem = false,
                reviewedVersesCount = 99,
                gameVisitStreak = 0,
                perfectGameSessions = 0,
            ),
        ).isFalse()

        assertThat(
            BadgeCatalog.resolveEarned(
                badgeId = 2,
                hasCompletedMemorizationPoem = false,
                reviewedVersesCount = 100,
                gameVisitStreak = 0,
                perfectGameSessions = 0,
            ),
        ).isTrue()
    }

    @Test
    fun `seven night streak badge uses game visit streak`() {
        assertThat(
            BadgeCatalog.resolveEarned(
                badgeId = 3,
                hasCompletedMemorizationPoem = false,
                reviewedVersesCount = 0,
                gameVisitStreak = 6,
                perfectGameSessions = 0,
            ),
        ).isFalse()

        assertThat(
            BadgeCatalog.resolveEarned(
                badgeId = 3,
                hasCompletedMemorizationPoem = false,
                reviewedVersesCount = 0,
                gameVisitStreak = 7,
                perfectGameSessions = 0,
            ),
        ).isTrue()
    }

    @Test
    fun `poetry lover badge is always unearned`() {
        assertThat(
            BadgeCatalog.resolveEarned(
                badgeId = 4,
                hasCompletedMemorizationPoem = true,
                reviewedVersesCount = 500,
                gameVisitStreak = 30,
                perfectGameSessions = 20,
            ),
        ).isFalse()
    }

    @Test
    fun `hafez star badge earned after five perfect game sessions`() {
        assertThat(
            BadgeCatalog.resolveEarned(
                badgeId = 5,
                hasCompletedMemorizationPoem = false,
                reviewedVersesCount = 0,
                gameVisitStreak = 0,
                perfectGameSessions = 4,
            ),
        ).isFalse()

        assertThat(
            BadgeCatalog.resolveEarned(
                badgeId = 5,
                hasCompletedMemorizationPoem = false,
                reviewedVersesCount = 0,
                gameVisitStreak = 0,
                perfectGameSessions = 5,
            ),
        ).isTrue()
    }

    @Test
    fun `toBadgeUi maps catalog fields and earn state`() {
        val badge = BadgeCatalog.badges.first { it.id == 2 }

        val ui =
            BadgeCatalog.toBadgeUi(
                badge = badge,
                hasCompletedMemorizationPoem = false,
                reviewedVersesCount = 100,
                gameVisitStreak = 0,
                perfectGameSessions = 0,
            )

        assertThat(ui.id).isEqualTo(2)
        assertThat(ui.name).isEqualTo("صد بیت")
        assertThat(ui.description).isEqualTo("۱۰۰ بیت حفظ کنید")
        assertThat(ui.isEarned).isTrue()
    }

    @Test
    fun `catalog contains five mock badges in order`() {
        assertThat(BadgeCatalog.badges.map { it.id }).isEqualTo(listOf(1, 2, 3, 4, 5))
    }
}
