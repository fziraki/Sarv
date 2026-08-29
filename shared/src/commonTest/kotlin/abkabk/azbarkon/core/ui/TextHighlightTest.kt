package abkabk.azbarkon.core.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class TextHighlightTest {
    private val highlightStyle = SpanStyle(color = Color.Red)

    @Test
    fun `returns plain text when query is blank`() {
        val result = buildHighlightedAnnotatedString(
            text = "الا یا ایها الساقی",
            query = "   ",
            highlightStyle = highlightStyle,
        )

        assertThat(result.text).isEqualTo("الا یا ایها الساقی")
        assertThat(result.spanStyles).isEqualTo(emptyList())
    }

    @Test
    fun `highlights all case-insensitive matches`() {
        val result = buildHighlightedAnnotatedString(
            text = "abc Abc xABCy",
            query = "abc",
            highlightStyle = highlightStyle,
        )

        assertThat(result.text).isEqualTo("abc Abc xABCy")
        assertThat(result.spanStyles.size).isEqualTo(3)
        assertThat(result.spanStyles[0].start).isEqualTo(0)
        assertThat(result.spanStyles[0].end).isEqualTo(3)
        assertThat(result.spanStyles[1].start).isEqualTo(4)
        assertThat(result.spanStyles[1].end).isEqualTo(7)
        assertThat(result.spanStyles[2].start).isEqualTo(9)
        assertThat(result.spanStyles[2].end).isEqualTo(12)
        assertThat(result.spanStyles[0].item.color).isEqualTo(Color.Red)
        assertThat(result.spanStyles[0].item.background).isEqualTo(Color.Unspecified)
    }

    @Test
    fun `returns plain text when query does not match`() {
        val result = buildHighlightedAnnotatedString(
            text = "بیت اول",
            query = "زلف",
            highlightStyle = highlightStyle,
        )

        assertThat(result.text).isEqualTo("بیت اول")
        assertThat(result.spanStyles).isEqualTo(emptyList())
    }
}
