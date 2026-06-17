package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.PoetCategoryNode

fun collectCatIdsInSubtree(
    rootCatId: Int,
    allCategories: List<CatNode>,
): Set<Int> {
    val childrenByParent = allCategories.groupBy { it.parentId }
    val result = mutableSetOf<Int>()

    fun collect(catId: Int) {
        result.add(catId)
        childrenByParent[catId]?.forEach { child ->
            collect(child.id)
        }
    }

    collect(rootCatId)
    return result
}

fun collectCatIdsInSubtreeFromTree(
    rootCatId: Int,
    categories: List<PoetCategoryNode>,
): Set<Int> {
    fun findNode(
        id: Int,
        nodes: List<PoetCategoryNode>,
    ): PoetCategoryNode? {
        for (node in nodes) {
            if (node.id == id) return node
            findNode(id, node.children)?.let { return it }
        }
        return null
    }

    fun collectFromNode(node: PoetCategoryNode): Set<Int> =
        buildSet {
            add(node.id)
            node.children.forEach { child ->
                addAll(collectFromNode(child))
            }
        }

    val rootNode = findNode(rootCatId, categories) ?: return setOf(rootCatId)
    return collectFromNode(rootNode)
}
