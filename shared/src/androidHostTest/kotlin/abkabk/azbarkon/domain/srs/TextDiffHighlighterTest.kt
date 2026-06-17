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

    @Test
    fun `ignores arabic diacritics when comparing`() {
        val withDiacritics = "بِخُرْ"
        val withoutDiacritics = "بخر"

        val grade = TextDiffHighlighter.suggestGradeFromChars(withDiacritics, withoutDiacritics)

        assertThat(grade).isEqualTo(abkabk.azbarkon.domain.model.memorization.SrsGrade.EASY)
    }

    @Test
    fun `normalizeForComparison strips combining marks`() {
        val normalized = TextDiffHighlighter.normalizeForComparison("بِخُرْ")

        assertThat(normalized).isEqualTo("بخر")
    }

    @Test
    fun `char match ignores spaces and punctuation`() {
        val grade =
            TextDiffHighlighter.suggestGradeFromChars(
                expected = "ولی افتاد مشکل‌ها",
                actual = "ولی، افتاد! مشکلها",
            )

        assertThat(grade).isEqualTo(abkabk.azbarkon.domain.model.memorization.SrsGrade.EASY)
    }

    @Test
    fun `char match partial answer suggests lower grade`() {
        val grade =
            TextDiffHighlighter.suggestGradeFromChars(
                expected = "ولی افتاد مشکل‌ها",
                actual = "ولی",
            )

        assertThat(grade).isEqualTo(abkabk.azbarkon.domain.model.memorization.SrsGrade.AGAIN)
    }

    @Test
    fun `diffUserWords colors user words only`() {
        val tokens = TextDiffHighlighter.diffUserWords("مصرع دوم", "مصرع دوم")

        assertThat(tokens).isEqualTo(
            listOf(
                DiffToken("مصرع", DiffTokenType.CORRECT),
                DiffToken("دوم", DiffTokenType.CORRECT),
            ),
        )
    }

    @Test
    fun `diffUserWords marks wrong user word`() {
        val tokens = TextDiffHighlighter.diffUserWords("مصرع دوم", "مصرع غلط")

        assertThat(tokens[0].type).isEqualTo(DiffTokenType.CORRECT)
        assertThat(tokens[1].type).isEqualTo(DiffTokenType.WRONG)
    }

    @Test
    fun `diffUserWords ignores extra spaces between words`() {
        val tokens = TextDiffHighlighter.diffUserWords("سه چهار", "سه  چهار")

        assertThat(tokens).isEqualTo(
            listOf(
                DiffToken("سه", DiffTokenType.CORRECT),
                DiffToken("چهار", DiffTokenType.CORRECT),
            ),
        )
    }

    @Test
    fun `diffUserWords matches letters despite punctuation in actual`() {
        val tokens = TextDiffHighlighter.diffUserWords("ولی افتاد", "ولی، افتاد!")

        assertThat(tokens.all { it.type == DiffTokenType.CORRECT }).isEqualTo(true)
    }
}
