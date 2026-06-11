package abkabk.azbarkon.domain.srs

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import org.junit.jupiter.api.Test

class TextDiffHighlighterTest {
    @Test
    fun `perfect match suggests easy`() {
        val grade = TextDiffHighlighter.suggestGrade("hello world", "hello world")

        assertThat(grade).isEqualTo(abkabk.azbarkon.domain.model.memorization.SrsGrade.EASY)
    }

    @Test
    fun `marks missing and wrong tokens`() {
        val tokens = TextDiffHighlighter.diff("one two three", "one wrong")

        assertThat(tokens.isNotEmpty()).isEqualTo(true)
        assertThat(tokens.count { it.type == DiffTokenType.CORRECT }).isEqualTo(1)
        assertThat(tokens.any { it.type == DiffTokenType.WRONG }).isEqualTo(true)
        assertThat(tokens.any { it.type == DiffTokenType.MISSING }).isEqualTo(true)
    }
}
