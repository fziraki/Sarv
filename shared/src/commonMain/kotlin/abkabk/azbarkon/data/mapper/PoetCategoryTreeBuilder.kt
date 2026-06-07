package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.PoetCategoryNode

fun buildPoetCategoryTree(
    rootCatId: Int,
    categories: List<CatNode>,
): List<PoetCategoryNode> {
    val childrenByParent = categories.groupBy { it.parentId }

    fun buildNodes(parentId: Int): List<PoetCategoryNode> =
        (childrenByParent[parentId] ?: emptyList()).map { category ->
            PoetCategoryNode(
                id = category.id,
                text = category.text,
                url = category.url,
                children = buildNodes(category.id),
            )
        }

    return buildNodes(rootCatId)
}
