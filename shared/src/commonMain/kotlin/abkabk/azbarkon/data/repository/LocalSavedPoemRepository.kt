package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.platform.KeyValueStore
import abkabk.azbarkon.domain.repository.SavedPoemRepository

class LocalSavedPoemRepository(
    private val keyValueStore: KeyValueStore,
) : SavedPoemRepository {
    override fun getLikedIds(): Set<Int> = likedIds()

    override fun getBookmarkedIds(): Set<Int> = bookmarkedIds()

    override fun isLiked(poemId: Int): Boolean = poemId in likedIds()

    override fun isBookmarked(poemId: Int): Boolean = poemId in bookmarkedIds()

    override fun toggleLike(poemId: Int): Boolean {
        val updated = likedIds().toggle(poemId)
        keyValueStore.putIntSet(KEY_LIKED, updated)
        return poemId in updated
    }

    override fun toggleBookmark(poemId: Int): Boolean {
        val updated = bookmarkedIds().toggle(poemId)
        keyValueStore.putIntSet(KEY_BOOKMARKED, updated)
        return poemId in updated
    }

    override fun removeLike(poemId: Int) {
        keyValueStore.putIntSet(KEY_LIKED, likedIds() - poemId)
    }

    override fun removeBookmark(poemId: Int) {
        keyValueStore.putIntSet(KEY_BOOKMARKED, bookmarkedIds() - poemId)
    }

    override fun clearLiked() {
        keyValueStore.putIntSet(KEY_LIKED, emptySet())
    }

    override fun clearBookmarked() {
        keyValueStore.putIntSet(KEY_BOOKMARKED, emptySet())
    }

    private fun likedIds(): Set<Int> = keyValueStore.getIntSet(KEY_LIKED)

    private fun bookmarkedIds(): Set<Int> = keyValueStore.getIntSet(KEY_BOOKMARKED)

    private fun Set<Int>.toggle(id: Int): Set<Int> =
        if (id in this) {
            this - id
        } else {
            this + id
        }

    private companion object {
        const val KEY_LIKED = "liked_poem_ids"
        const val KEY_BOOKMARKED = "bookmarked_poem_ids"
    }
}
