package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.datasource.ChatLocalDataSource
import abkabk.azbarkon.domain.model.ChatDistich
import abkabk.azbarkon.domain.repository.ChatRepository

class OfflineFirstChatRepository(
    private val localDataSource: ChatLocalDataSource,
) : ChatRepository {
    override suspend fun findDistichByLastLetter(
        poetId: Int,
        lastLetter: Char,
    ): Result<ChatDistich, DataError.Local> =
        localDataSource.findDistichByPrefix(
            poetId = poetId,
            prefix = "$lastLetter%",
        )
}
