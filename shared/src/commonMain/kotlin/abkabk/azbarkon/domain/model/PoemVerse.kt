package abkabk.azbarkon.domain.model

const val PARAGRAPH_POSITION = 3

data class PoemVerse(
    val poemId: Int,
    val vorder: Int,
    val position: Int,
    val text: String,
)
