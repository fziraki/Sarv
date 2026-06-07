package abkabk.azbarkon.domain.datasource

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.PoemDetail
import abkabk.azbarkon.domain.model.PoemSummary

interface PoemLocalDataSource {
    suspend fun getPoemsByCatId(catId: Int): Result<List<PoemSummary>, DataError.Local>

    suspend fun getPoemDetail(poemId: Int): Result<PoemDetail, DataError.Local>
}
