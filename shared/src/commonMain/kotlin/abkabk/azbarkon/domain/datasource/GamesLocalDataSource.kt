package abkabk.azbarkon.domain.datasource

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.games.GameGenerationCache
import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.domain.model.games.GameType

interface GamesLocalDataSource {
    suspend fun buildPoemBundle(
        gameType: GameType,
        quizIndex: Int,
        cache: GameGenerationCache,
    ): Result<Unit, DataError.Local>

    suspend fun generateQuestion(
        gameType: GameType,
        quizIndex: Int,
        seed: Long,
        cache: GameGenerationCache,
    ): Result<GameQuestion, DataError.Local>

    suspend fun getAllPoets(): Result<List<Poet>, DataError.Local>
}
