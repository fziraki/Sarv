package abkabk.azbarkon.domain.datasource

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.ChatDistich

interface ChatLocalDataSource {
    suspend fun findDistichByPrefix(
        poetId: Int,
        prefix: String,
    ): Result<ChatDistich, DataError.Local>
}
