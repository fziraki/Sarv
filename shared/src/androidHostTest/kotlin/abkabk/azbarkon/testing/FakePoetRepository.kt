package abkabk.azbarkon.testing

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.repository.PoetRepository

class FakePoetRepository : PoetRepository {
    var poets: List<Poet> = emptyList()
    var shouldFail: Boolean = false

    override suspend fun getPoets(): Result<List<Poet>, DataError.Local> =
        if (shouldFail) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            Result.Success(poets)
        }
}
