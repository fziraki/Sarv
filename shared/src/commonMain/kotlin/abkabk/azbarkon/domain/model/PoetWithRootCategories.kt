package abkabk.azbarkon.domain.model

data class PoetWithRootCategories(
    val poet: Poet,
    val rootCategories: List<CatNode>,
    val allCategories: List<CatNode> = emptyList(),
)

fun PoetWithRootCategories.hasWorks(): Boolean = rootCategories.isNotEmpty()
