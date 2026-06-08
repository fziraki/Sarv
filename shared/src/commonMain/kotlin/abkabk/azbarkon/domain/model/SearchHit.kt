package abkabk.azbarkon.domain.model

data class SearchHit(
    val poemId: Int,
    val poemTitle: String,
    val poetName: String,
    val categoryName: String,
    val verseText: String,
    val verseOrder: Int,
)

data class SearchPage(
    val hits: List<SearchHit>,
    val totalCount: Int,
)
