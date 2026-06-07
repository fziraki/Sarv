package abkabk.azbarkon.testing

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.MyPoemSummary
import abkabk.azbarkon.domain.model.PoemDetail
import abkabk.azbarkon.domain.model.PoemSummary
import abkabk.azbarkon.domain.repository.PoemRepository

class FakePoemRepository : PoemRepository {
    var poemsByCatId: Map<Int, List<PoemSummary>> = emptyMap()
    var poemsByIds: Map<Int, MyPoemSummary> = emptyMap()
    var poemDetails: Map<Int, PoemDetail> = emptyMap()
    var shouldFail: Boolean = false
    var shouldFailByIds: Boolean = false
    var shouldFailDetail: Boolean = false

    override suspend fun getPoemsByCatId(catId: Int): Result<List<PoemSummary>, DataError.Local> =
        if (shouldFail) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            Result.Success(poemsByCatId[catId].orEmpty())
        }

    override suspend fun getPoemsByIds(ids: Set<Int>): Result<List<MyPoemSummary>, DataError.Local> =
        if (shouldFailByIds) {
            Result.Error(DataError.Local.UNKNOWN)
        } else if (ids.isEmpty()) {
            Result.Success(emptyList())
        } else {
            Result.Success(
                ids.mapNotNull { poemsByIds[it] },
            )
        }

    override suspend fun getPoemDetail(poemId: Int): Result<PoemDetail, DataError.Local> =
        if (shouldFailDetail) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            poemDetails[poemId]?.let { Result.Success(it) }
                ?: Result.Error(DataError.Local.NOT_FOUND)
        }
}
