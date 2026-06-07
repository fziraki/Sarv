package abkabk.azbarkon.domain.model

data class PoemDetail(
    val id: Int,
    val title: String,
    val poetName: String,
    val categoryName: String,
    val verses: List<PoemVerse>,
)
