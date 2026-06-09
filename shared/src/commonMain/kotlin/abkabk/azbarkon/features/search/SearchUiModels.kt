package abkabk.azbarkon.features.search

import abkabk.azbarkon.domain.model.PoetCategoryNode
import abkabk.azbarkon.domain.model.SearchHit
import androidx.compose.runtime.Stable

@Stable
data class SearchPoetOptionUi(
    val id: Int?,
    val name: String,
)

@Stable
data class SearchCategoryOptionUi(
    val id: Int?,
    val title: String,
    val depth: Int,
)

@Stable
data class SearchResultUi(
    val poemId: Int,
    val poemTitle: String,
    val poetName: String,
    val categoryName: String,
    val verseText: String,
    val key: String,
)

fun SearchHit.toSearchResultUi(): SearchResultUi =
    SearchResultUi(
        poemId = poemId,
        poemTitle = poemTitle,
        poetName = poetName,
        categoryName = categoryName,
        verseText = verseText,
        key = "$poemId-$verseOrder",
    )

fun flattenAllSearchCategories(
    nodes: List<PoetCategoryNode>,
    depth: Int = 0,
): List<SearchCategoryOptionUi> =
    buildList {
        nodes.forEach { node ->
            add(
                SearchCategoryOptionUi(
                    id = node.id,
                    title = node.text,
                    depth = depth,
                ),
            )
            addAll(flattenAllSearchCategories(node.children, depth + 1))
        }
    }
