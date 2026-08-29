package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.PoetCategoryNode
import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isEqualTo
import kotlin.test.Test

class CategorySubtreeTest {
    @Test
    fun `collectCatIdsInSubtree includes root and descendants`() {
        val categories =
            listOf(
                cat(id = 9, parentId = 0),
                cat(id = 24, parentId = 9, text = "غزلیات"),
                cat(id = 101, parentId = 24),
                cat(id = 25, parentId = 9, text = "رباعیات"),
            )

        assertThat(collectCatIdsInSubtree(rootCatId = 24, allCategories = categories))
            .isEqualTo(setOf(24, 101))
    }

    @Test
    fun `collectCatIdsInSubtreeFromTree includes nested descendants`() {
        val tree =
            listOf(
                PoetCategoryNode(
                    id = 24,
                    text = "غزلیات",
                    url = "/ghazals",
                    children =
                        listOf(
                            PoetCategoryNode(
                                id = 101,
                                text = "غزل ۱",
                                url = "/ghazals/1",
                                children = emptyList(),
                            ),
                        ),
                ),
                PoetCategoryNode(
                    id = 25,
                    text = "رباعیات",
                    url = "/rubaiyat",
                    children = emptyList(),
                ),
            )

        assertThat(collectCatIdsInSubtreeFromTree(rootCatId = 24, categories = tree))
            .containsExactlyInAnyOrder(24, 101)
    }

    private fun cat(
        id: Int,
        parentId: Int,
        text: String = "category",
    ): CatNode =
        CatNode(
            id = id,
            poetId = 2,
            text = text,
            parentId = parentId,
            url = "/$id",
        )
}
