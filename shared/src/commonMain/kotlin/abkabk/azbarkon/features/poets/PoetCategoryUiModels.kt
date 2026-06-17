package abkabk.azbarkon.features.poets

import abkabk.azbarkon.domain.model.PoetCategoryNode
import androidx.compose.runtime.Stable

@Stable
data class PoetCategoryRowUi(
    val id: Int,
    val title: String,
    val depth: Int,
    val isParent: Boolean,
    val isExpanded: Boolean,
)

fun flattenPoetCategories(
    nodes: List<PoetCategoryNode>,
    expandedCategoryIds: Set<Int>,
    depth: Int = 0,
): List<PoetCategoryRowUi> =
    buildList {
        nodes.forEach { node ->
            val isParent = node.children.isNotEmpty()
            val isExpanded = node.id in expandedCategoryIds
            add(
                PoetCategoryRowUi(
                    id = node.id,
                    title = node.text,
                    depth = depth,
                    isParent = isParent,
                    isExpanded = isExpanded,
                ),
            )
            if (isParent && isExpanded) {
                addAll(
                    flattenPoetCategories(
                        nodes = node.children,
                        expandedCategoryIds = expandedCategoryIds,
                        depth = depth + 1,
                    ),
                )
            }
        }
    }
