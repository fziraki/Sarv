package abkabk.azbarkon.domain.model

data class CatNode(
    val id: Int,
    val poetId: Int,
    val text: String,
    val parentId: Int,
    val url: String,
)
