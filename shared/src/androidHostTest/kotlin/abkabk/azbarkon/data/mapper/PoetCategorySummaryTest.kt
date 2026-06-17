package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.CatNode
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class PoetCategorySummaryTest {
    @Test
    fun `empty categories returns empty summary`() {
        assertThat(rootCategoriesSummary(emptyList()) { first, count -> "$first|$count" }).isEqualTo("")
    }

    @Test
    fun `single category returns its name`() {
        val categories = listOf(cat(text = "غزلیات"))

        assertThat(rootCategoriesSummary(categories) { first, count -> "$first|$count" }).isEqualTo("غزلیات")
    }

    @Test
    fun `multiple categories uses shortest name and other count`() {
        val categories =
            listOf(
                cat(text = "غزلیات"),
                cat(text = "قطعات"),
                cat(text = "رباعیات"),
                cat(text = "قصاید"),
                cat(text = "اشعار منتسب"),
            )

        assertThat(
            rootCategoriesSummary(categories) { first, count -> "$first و $count اثر دیگر" },
        ).isEqualTo("قطعات و 4 اثر دیگر")
    }

    @Test
    fun `picks shortest name when first category is longest`() {
        val categories =
            listOf(
                cat(text = "اشعار منتسب"),
                cat(text = "غزلیات"),
                cat(text = "قطعات"),
            )

        assertThat(
            rootCategoriesSummary(categories) { first, count -> "$first و $count اثر دیگر" },
        ).isEqualTo("قطعات و 2 اثر دیگر")
    }

    private fun cat(text: String): CatNode =
        CatNode(
            id = 1,
            poetId = 2,
            text = text,
            parentId = 9,
            url = "/test",
        )
}
