package abkabk.azbarkon.testing

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.repository.PoetRepository

class FakePoetRepository : PoetRepository {
    var localPoets: List<Poet> = emptyList()
    var shouldFailLocal: Boolean = false

    override suspend fun getPoets(): Result<List<Poet>, DataError> = Result.Success(localPoets)

    override suspend fun getPoetsLocally(): Result<List<Poet>, DataError.Local> =
        if (shouldFailLocal) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            Result.Success(localPoets)
        }
}
