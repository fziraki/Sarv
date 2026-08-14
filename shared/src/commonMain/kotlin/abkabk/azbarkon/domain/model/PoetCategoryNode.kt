package abkabk.azbarkon.domain.model

data class PoetCategoryNode(
    val id: Int,
    val text: String,
    val url: String,
    val children: List<PoetCategoryNode> = emptyList(),
)

fun PoetCategoryNode.hasCategory(text: String): Boolean =
    this.text == text || children.any { it.hasCategory(text) }
