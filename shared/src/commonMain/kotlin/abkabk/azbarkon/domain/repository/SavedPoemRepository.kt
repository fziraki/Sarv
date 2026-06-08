package abkabk.azbarkon.domain.repository

interface SavedPoemRepository {
    fun getLikedIds(): Set<Int>

    fun getBookmarkedIds(): Set<Int>

    fun isLiked(poemId: Int): Boolean

    fun isBookmarked(poemId: Int): Boolean

    fun toggleLike(poemId: Int): Boolean

    fun toggleBookmark(poemId: Int): Boolean

    fun removeLike(poemId: Int)

    fun removeBookmark(poemId: Int)

    fun clearLiked()

    fun clearBookmarked()
}
