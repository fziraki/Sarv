package abkabk.azbarkon.data.generator

import abkabk.azbarkon.domain.model.games.GameType
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

class GameSessionPoolBuilderTest {
    @Test
    fun `extracts distichs from paired verses`() {
        val verses =
            listOf(
                VerseRow(vorder = 0, position = 0, text = "line one"),
                VerseRow(vorder = 1, position = 0, text = "line two"),
                VerseRow(vorder = 2, position = 0, text = "line three"),
                VerseRow(vorder = 3, position = 0, text = "line four"),
            )

        val extraction =
            GameSessionPoolBuilder.extractFromVerses(
                poemId = 10,
                poetId = 2,
                poetName = "حافظ",
                verses = verses,
            )

        assertThat(extraction.distichs).hasSize(3)
        assertThat(extraction.distichs.first().firstHemistich).isEqualTo("line one")
        assertThat(extraction.distichs.first().secondHemistich).isEqualTo("line two")
    }

    @Test
    fun `rejects poems below minimum distich count`() {
        val verses =
            listOf(
                VerseRow(vorder = 0, position = 0, text = "line one"),
                VerseRow(vorder = 1, position = 0, text = "line two"),
            )

        val extraction =
            GameSessionPoolBuilder.extractFromVerses(
                poemId = 10,
                poetId = 2,
                poetName = "حافظ",
                verses = verses,
            )

        assertThat(
            GameSessionPoolBuilder.isPoemAcceptable(
                gameType = GameType.NEXT_VERSE,
                distichCount = extraction.distichs.size,
                organizeCount = extraction.organizeWindows.size,
            ),
        ).isFalse()
    }

    @Test
    fun `accepts organize poem when windows exist`() {
        assertThat(
            GameSessionPoolBuilder.isPoemAcceptable(
                gameType = GameType.ORGANIZE_POEM,
                distichCount = 0,
                organizeCount = 1,
            ),
        ).isTrue()
    }

    @Test
    fun `extracts organize windows from four consecutive lines`() {
        val verses =
            listOf(
                VerseRow(vorder = 0, position = 0, text = "a"),
                VerseRow(vorder = 1, position = 0, text = "b"),
                VerseRow(vorder = 2, position = 0, text = "c"),
                VerseRow(vorder = 3, position = 0, text = "d"),
                VerseRow(vorder = 4, position = 0, text = "e"),
            )

        val extraction =
            GameSessionPoolBuilder.extractFromVerses(
                poemId = 11,
                poetId = 2,
                poetName = "حافظ",
                verses = verses,
            )

        assertThat(extraction.organizeWindows).hasSize(2)
        assertThat(extraction.organizeWindows.first().lines).isEqualTo(listOf("a", "b", "c", "d"))
    }
}
