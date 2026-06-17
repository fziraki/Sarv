package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.CatNode
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class PoetCategoryTreeBuilderTest {
    @Test
    fun `builds nested category tree from flat nodes`() {
        val categories =
            listOf(
                cat(id = 24, poetId = 2, text = "غزلیات", parentId = 9),
                cat(id = 100, poetId = 2, text = "غزل ۱", parentId = 24),
                cat(id = 25, poetId = 2, text = "قطعات", parentId = 9),
            )

        val tree = buildPoetCategoryTree(rootCatId = 9, categories = categories)

        assertThat(tree).hasSize(2)
        assertThat(tree.first().text).isEqualTo("غزلیات")
        assertThat(tree.first().children).hasSize(1)
        assertThat(tree.first().children.first().text).isEqualTo("غزل ۱")
        assertThat(tree.last().text).isEqualTo("قطعات")
        assertThat(tree.last().children).hasSize(0)
    }

    @Test
    fun `returns empty list when root has no children`() {
        val tree = buildPoetCategoryTree(rootCatId = 9, categories = emptyList())

        assertThat(tree).hasSize(0)
    }

    private fun cat(
        id: Int,
        poetId: Int,
        text: String,
        parentId: Int,
    ): CatNode =
        CatNode(
            id = id,
            poetId = poetId,
            text = text,
            parentId = parentId,
            url = "/test",
        )
}
