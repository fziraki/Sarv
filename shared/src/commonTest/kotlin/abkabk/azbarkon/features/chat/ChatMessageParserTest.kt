package abkabk.azbarkon.features.chat

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test

class ChatMessageParserTest {
    @Test
    fun `extracts last persian letter from message`() {
        assertThat(extractLastPersianLetter("دلم گرفته و خسته‌ام")).isEqualTo('م')
    }

    @Test
    fun `strips trailing whitespace and punctuation`() {
        assertThat(extractLastPersianLetter("سلام؟!  ")).isEqualTo('م')
    }

    @Test
    fun `returns null for empty input`() {
        assertThat(extractLastPersianLetter("")).isNull()
    }

    @Test
    fun `returns null for punctuation only`() {
        assertThat(extractLastPersianLetter("؟!،")).isNull()
    }

    @Test
    fun `returns null for non persian script only`() {
        assertThat(extractLastPersianLetter("hello!!!")).isNull()
    }
}
