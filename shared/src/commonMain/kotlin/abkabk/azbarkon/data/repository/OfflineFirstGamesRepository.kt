package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.datasource.GamesLocalDataSource
import abkabk.azbarkon.domain.model.games.GameGenerationCache
import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.domain.model.games.GameType
import abkabk.azbarkon.domain.repository.GamesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OfflineFirstGamesRepository(
    private val localDataSource: GamesLocalDataSource,
) : GamesRepository {
    override fun createGenerationCache(): GameGenerationCache = GameGenerationCache()

    override suspend fun generateQuestion(
        gameType: GameType,
        sessionSeed: Long,
        quizIndex: Int,
        cache: GameGenerationCache,
    ): Result<GameQuestion, DataError.Local> =
        withContext(Dispatchers.Default) {
            ensurePoemBundle(gameType, cache, quizIndex)?.let { error ->
                return@withContext Result.Error(error)
            }
            localDataSource.generateQuestion(
                gameType = gameType,
                quizIndex = quizIndex,
                seed = questionSeed(sessionSeed, quizIndex),
                cache = cache,
            )
        }

    override suspend fun generateQuizBatch(
        gameType: GameType,
        seed: Long,
        count: Int,
    ): Result<List<GameQuestion>, DataError.Local> =
        withContext(Dispatchers.Default) {
            val cache = GameGenerationCache()
            if (gameType == GameType.FIND_POET) {
                ensurePoetsCached(cache)?.let { error ->
                    return@withContext Result.Error(error)
                }
            }
            val questions = mutableListOf<GameQuestion>()
            repeat(count) { index ->
                ensurePoemBundle(gameType, cache, index)?.let { error ->
                    return@withContext Result.Error(error)
                }
                when (
                    val result =
                        localDataSource.generateQuestion(
                            gameType = gameType,
                            quizIndex = index,
                            seed = questionSeed(seed, index),
                            cache = cache,
                        )
                ) {
                    is Result.Success -> questions += result.data
                    is Result.Error -> return@withContext result
                }
            }
            Result.Success(questions)
        }

    private suspend fun ensurePoemBundle(
        gameType: GameType,
        cache: GameGenerationCache,
        quizIndex: Int,
    ): DataError.Local? {
        if (gameType == GameType.FIND_POET) {
            ensurePoetsCached(cache)?.let { return it }
        }
        if (cache.hasBundle(quizIndex)) return null
        return when (val result = localDataSource.buildPoemBundle(gameType, quizIndex, cache)) {
            is Result.Success -> null
            is Result.Error -> result.error
        }
    }

    private suspend fun ensurePoetsCached(cache: GameGenerationCache): DataError.Local? {
        if (cache.cachedPoets != null) return null
        return when (val poetsResult = localDataSource.getAllPoets()) {
            is Result.Success -> {
                cache.cachedPoets = poetsResult.data
                null
            }
            is Result.Error -> poetsResult.error
        }
    }

    private fun questionSeed(
        sessionSeed: Long,
        quizIndex: Int,
    ): Long = sessionSeed + quizIndex * QUESTION_SEED_STEP

    private companion object {
        const val QUESTION_SEED_STEP = 7919L
    }
}
