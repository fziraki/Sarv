package abkabk.azbarkon.domain.srs

import abkabk.azbarkon.domain.model.PoemVerse
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

class CardGeneratorTest {
    @Test
    fun `groups verses into couplet cards`() {
        val verses =
            listOf(
                PoemVerse(poemId = 1, vorder = 0, position = 0, text = "مصرع اول"),
                PoemVerse(poemId = 1, vorder = 0, position = 1, text = "مصرع دوم"),
                PoemVerse(poemId = 1, vorder = 1, position = 0, text = "بیت دوم راست"),
                PoemVerse(poemId = 1, vorder = 1, position = 1, text = "بیت دوم چپ"),
            )

        val cards = CardGenerator.buildGeneratedCards(verses)

        assertThat(cards).hasSize(2)
        assertThat(cards[0].front).isEqualTo("مصرع اول\n...")
        assertThat(cards[0].back).isEqualTo("مصرع اول\nمصرع دوم")
        assertThat(cards[1].back).isEqualTo("بیت دوم راست\nبیت دوم چپ")
    }

    @Test
    fun `single line poem creates masked front`() {
        val verses =
            listOf(
                PoemVerse(poemId = 1, vorder = 0, position = 0, text = "یک دو سه چهار"),
            )

        val cards = CardGenerator.buildGeneratedCards(verses)

        assertThat(cards).hasSize(1)
        assertThat(cards[0].front.contains("...")).isTrue()
    }

    @Test
    fun `expectedContinuation returns hidden couplet line`() {
        val front = "مصرع اول\n..."
        val back = "مصرع اول\nمصرع دوم"

        assertThat(CardGenerator.expectedContinuation(front, back)).isEqualTo("مصرع دوم")
    }

    @Test
    fun `expectedContinuation returns hidden words for masked single line`() {
        val front = "یک دو ..."
        val back = "یک دو سه چهار"

        assertThat(CardGenerator.expectedContinuation(front, back)).isEqualTo("سه چهار")
    }

    @Test
    fun `revealedFrontParts replaces couplet ellipsis`() {
        val parts =
            CardGenerator.revealedFrontParts(
                front = "مصرع اول\n...",
                continuation = "مصرع دوم",
            )

        assertThat(parts.prefix).isEqualTo("مصرع اول\n ")
        assertThat(parts.continuation).isEqualTo("مصرع دوم")
    }

    @Test
    fun `revealedFrontParts replaces masked ellipsis`() {
        val parts =
            CardGenerator.revealedFrontParts(
                front = "یک دو ...",
                continuation = "سه چهار",
            )

        assertThat(parts.prefix).isEqualTo("یک دو ")
        assertThat(parts.continuation).isEqualTo("سه چهار")
        assertThat(parts.suffix).isEqualTo("")
    }
}
