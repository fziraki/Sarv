package abkabk.azbarkon.testing

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.PoemDetail
import abkabk.azbarkon.domain.model.PoemSummary
import abkabk.azbarkon.domain.repository.PoemRepository

class FakePoemRepository : PoemRepository {
    var poemsByCatId: Map<Int, List<PoemSummary>> = emptyMap()
    var poemDetails: Map<Int, PoemDetail> = emptyMap()
    var shouldFail: Boolean = false
    var shouldFailDetail: Boolean = false

    override suspend fun getPoemsByCatId(catId: Int): Result<List<PoemSummary>, DataError.Local> =
        if (shouldFail) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            Result.Success(poemsByCatId[catId].orEmpty())
        }

    override suspend fun getPoemDetail(poemId: Int): Result<PoemDetail, DataError.Local> =
        if (shouldFailDetail) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            poemDetails[poemId]?.let { Result.Success(it) }
                ?: Result.Error(DataError.Local.NOT_FOUND)
        }
}
