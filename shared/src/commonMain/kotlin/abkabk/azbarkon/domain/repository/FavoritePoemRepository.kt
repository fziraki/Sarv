package abkabk.azbarkon.domain.repository

interface FavoritePoemRepository {
    fun isLiked(poemId: Int): Boolean

    fun isBookmarked(poemId: Int): Boolean

    fun toggleLike(poemId: Int): Boolean

    fun toggleBookmark(poemId: Int): Boolean
}
