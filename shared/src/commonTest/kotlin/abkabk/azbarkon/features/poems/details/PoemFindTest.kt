package abkabk.azbarkon.features.poems.details

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test

class PoemFindTest {
    @Test
    fun `findFirstMatchingVerse returns first containing query`() {
        val verses =
            listOf(
                PoemVerseUi(
                    id = "1-0",
                    text = "بیت اول",
                    positionType = PoemVersePositionType.Right,
                ),
                PoemVerseUi(
                    id = "1-1",
                    text = "بیت دوم",
                    positionType = PoemVersePositionType.Left,
                ),
            )

        assertThat(findFirstMatchingVerse(verses, "دوم")?.id).isEqualTo("1-1")
    }

    @Test
    fun `findFirstMatchingVerse returns null when no match`() {
        val verses =
            listOf(
                PoemVerseUi(
                    id = "1-0",
                    text = "بیت اول",
                    positionType = PoemVersePositionType.Right,
                ),
            )

        assertThat(findFirstMatchingVerse(verses, "زلف")).isNull()
    }
}
