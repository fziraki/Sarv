package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.Poet
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class PoetWorkResolverTest {
    private val hafez =
        Poet(
            id = 2,
            name = "حافظ شیرازی",
            description = null,
            rootCatId = 9,
            imageUrl = null,
        )

    private val saadi =
        Poet(
            id = 7,
            name = "سعدی شیرازی",
            description = null,
            rootCatId = 118,
            imageUrl = null,
        )

    @Test
    fun `section children resolve to single divan work`() {
        val works =
            resolvePoetWorks(
                poet = hafez,
                rootChildren =
                    listOf(
                        cat(id = 24, poetId = 2, text = "غزلیات", parentId = 9),
                        cat(id = 25, poetId = 2, text = "قطعات", parentId = 9),
                    ),
            )

        assertThat(works).hasSize(1)
        assertThat(works.first().id).isEqualTo(9)
        assertThat(works.first().title).isEqualTo("دیوان حافظ")
    }

    @Test
    fun `named children resolve to separate works`() {
        val works =
            resolvePoetWorks(
                poet = saadi,
                rootChildren =
                    listOf(
                        cat(id = 1665, poetId = 7, text = "گلستان", parentId = 118),
                        cat(id = 123, poetId = 7, text = "بوستان", parentId = 118),
                    ),
            )

        assertThat(works).hasSize(2)
        assertThat(works.map { it.title }).isEqualTo(listOf("گلستان", "بوستان"))
    }

    @Test
    fun `single named child resolves to one work`() {
        val works =
            resolvePoetWorks(
                poet =
                    Poet(
                        id = 4,
                        name = "فردوسی",
                        description = null,
                        rootCatId = 32,
                        imageUrl = null,
                    ),
                rootChildren =
                    listOf(
                        cat(id = 33, poetId = 4, text = "شاهنامه", parentId = 32),
                    ),
            )

        assertThat(works).hasSize(1)
        assertThat(works.first().title).isEqualTo("شاهنامه")
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
