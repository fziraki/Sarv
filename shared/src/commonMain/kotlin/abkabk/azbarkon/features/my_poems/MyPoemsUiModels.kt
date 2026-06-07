package abkabk.azbarkon.features.my_poems

import abkabk.azbarkon.domain.model.MyPoemSummary
import androidx.compose.runtime.Stable

enum class MyPoemsTab {
    Liked,
    Bookmarked,
}

@Stable
data class MyPoemItemUi(
    val id: Int,
    val title: String,
)

@Stable
data class CategoryGroupUi(
    val categoryName: String,
    val poems: List<MyPoemItemUi>,
)

@Stable
data class PoetGroupUi(
    val poetName: String,
    val categories: List<CategoryGroupUi>,
)

fun List<MyPoemSummary>.toPoetGroups(): List<PoetGroupUi> {
    val poetOrder = mutableListOf<String>()
    val poetMap = linkedMapOf<String, LinkedHashMap<String, MutableList<MyPoemItemUi>>>()

    for (poem in this) {
        if (poem.poetName !in poetMap) {
            poetOrder.add(poem.poetName)
            poetMap[poem.poetName] = linkedMapOf()
        }
        val categoryMap = poetMap.getValue(poem.poetName)
        categoryMap.getOrPut(poem.categoryName) { mutableListOf() }.add(
            MyPoemItemUi(
                id = poem.id,
                title = poem.title,
            ),
        )
    }

    return poetOrder.map { poetName ->
        val categories = poetMap.getValue(poetName)
        PoetGroupUi(
            poetName = poetName,
            categories =
                categories.map { (categoryName, poems) ->
                    CategoryGroupUi(
                        categoryName = categoryName,
                        poems = poems,
                    )
                },
        )
    }
}
