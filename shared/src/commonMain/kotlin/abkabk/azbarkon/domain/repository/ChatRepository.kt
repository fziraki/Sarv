package abkabk.azbarkon.domain.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.ChatDistich

interface ChatRepository {
    suspend fun findDistichByLastLetter(
        poetId: Int,
        lastLetter: Char,
    ): Result<ChatDistich, DataError.Local>
}
