package abkabk.azbarkon.testing

import abkabk.azbarkon.domain.repository.SavedPoemRepository

class FakeSavedPoemRepository : SavedPoemRepository {
    private val liked = mutableSetOf<Int>()
    private val bookmarked = mutableSetOf<Int>()

    override fun getLikedIds(): Set<Int> = liked.toSet()

    override fun getBookmarkedIds(): Set<Int> = bookmarked.toSet()

    override fun isLiked(poemId: Int): Boolean = poemId in liked

    override fun isBookmarked(poemId: Int): Boolean = poemId in bookmarked

    override fun toggleLike(poemId: Int): Boolean {
        if (poemId in liked) {
            liked.remove(poemId)
        } else {
            liked.add(poemId)
        }
        return poemId in liked
    }

    override fun toggleBookmark(poemId: Int): Boolean {
        if (poemId in bookmarked) {
            bookmarked.remove(poemId)
        } else {
            bookmarked.add(poemId)
        }
        return poemId in bookmarked
    }

    override fun removeLike(poemId: Int) {
        liked.remove(poemId)
    }

    override fun removeBookmark(poemId: Int) {
        bookmarked.remove(poemId)
    }

    override fun clearLiked() {
        liked.clear()
    }

    override fun clearBookmarked() {
        bookmarked.clear()
    }
}
