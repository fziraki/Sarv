package abkabk.azbarkon.testing

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetWithWorks
import abkabk.azbarkon.domain.repository.PoetRepository

class FakePoetRepository : PoetRepository {
    var poets: List<Poet> = emptyList()
    var poetsWithWorks: List<PoetWithWorks> = emptyList()
    var shouldFail: Boolean = false

    override suspend fun getPoets(): Result<List<Poet>, DataError.Local> =
        if (shouldFail) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            Result.Success(poets)
        }

    override suspend fun getPoetsWithWorks(): Result<List<PoetWithWorks>, DataError.Local> =
        if (shouldFail) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            Result.Success(poetsWithWorks)
        }

    override suspend fun getPoetWithWorks(poetId: Int): Result<PoetWithWorks, DataError.Local> =
        if (shouldFail) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            val poetWithWorks =
                poetsWithWorks.find { it.poet.id == poetId }
                    ?: return Result.Error(DataError.Local.UNKNOWN)
            Result.Success(poetWithWorks)
        }
}
