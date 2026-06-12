package abkabk.azbarkon.domain.model.games

import abkabk.azbarkon.domain.model.Poet

class GameGenerationCache {
    val poemBundles: MutableMap<Int, GamePoemBundle> = mutableMapOf()
    val usedPoemIds: MutableSet<Long> = mutableSetOf()
    val usedPoetIds: MutableSet<Long> = mutableSetOf()
    var cachedPoets: List<Poet>? = null

    fun hasBundle(quizIndex: Int): Boolean = quizIndex in poemBundles
}
