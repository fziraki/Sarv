package abkabk.azbarkon.domain.model

data class PoetWithCategories(
    val poet: Poet,
    val categories: List<PoetCategoryNode>,
)
