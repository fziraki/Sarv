package abkabk.azbarkon.domain.model

data class PoetCategoryNode(
    val id: Int,
    val text: String,
    val url: String,
    val children: List<PoetCategoryNode> = emptyList(),
)
