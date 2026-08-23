package abkabk.azbarkon.domain.model.games

import abkabk.azbarkon.domain.model.Poet

class GameGenerationCache {
    val poemBundles: MutableMap<Int, GamePoemBundle> = mutableMapOf()
    val usedPoemIds: MutableSet<Long> = mutableSetOf()
    val poetUseCount: MutableMap<Long, Int> = mutableMapOf()
    var cachedPoets: List<Poet>? = null

    fun hasBundle(quizIndex: Int): Boolean = quizIndex in poemBundles

    fun isPoetAvailable(poetId: Long): Boolean = (poetUseCount[poetId] ?: 0) < MAX_USES_PER_POET

    fun recordPoetUse(poetId: Long) {
        poetUseCount[poetId] = (poetUseCount[poetId] ?: 0) + 1
    }

    private companion object {
        const val MAX_USES_PER_POET = 2
    }
}
