package abkabk.azbarkon.domain.repository



import abkabk.azbarkon.core.domain.result.DataError

import abkabk.azbarkon.core.domain.result.Result

import abkabk.azbarkon.domain.model.games.GameGenerationCache

import abkabk.azbarkon.domain.model.games.GameQuestion

import abkabk.azbarkon.domain.model.games.GameType



interface GamesRepository {

    fun createGenerationCache(): GameGenerationCache



    suspend fun generateQuestion(

        gameType: GameType,

        sessionSeed: Long,

        quizIndex: Int,

        cache: GameGenerationCache,

    ): Result<GameQuestion, DataError.Local>



    suspend fun generateQuizBatch(

        gameType: GameType,

        seed: Long,

        count: Int = 10,

    ): Result<List<GameQuestion>, DataError.Local>

}

