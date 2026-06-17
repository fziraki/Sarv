package abkabk.azbarkon.domain.model

data class PoetWithRootCategories(
    val poet: Poet,
    val rootCategories: List<CatNode>,
)

fun PoetWithRootCategories.hasWorks(): Boolean = rootCategories.isNotEmpty()
