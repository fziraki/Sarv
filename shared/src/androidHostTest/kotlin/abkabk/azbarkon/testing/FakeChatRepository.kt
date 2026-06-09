package abkabk.azbarkon.testing

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.ChatDistich
import abkabk.azbarkon.domain.repository.ChatRepository

class FakeChatRepository : ChatRepository {
    var distich: ChatDistich =
        ChatDistich(
            poemId = 42,
            rightText = "مصرع اول",
            leftText = "مصرع دوم",
        )
    var shouldFail: Boolean = false
    var lastPoetId: Int? = null
    var lastLetter: Char? = null

    override suspend fun findDistichByLastLetter(
        poetId: Int,
        lastLetter: Char,
    ): Result<ChatDistich, DataError.Local> {
        lastPoetId = poetId
        this.lastLetter = lastLetter
        return if (shouldFail) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            Result.Success(distich)
        }
    }
}
