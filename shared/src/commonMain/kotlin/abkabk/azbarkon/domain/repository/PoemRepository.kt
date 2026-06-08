package abkabk.azbarkon.domain.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.MyPoemSummary
import abkabk.azbarkon.domain.model.PoemDetail
import abkabk.azbarkon.domain.model.PoemSummary

interface PoemRepository {
    suspend fun getPoemsByCatId(catId: Int): Result<List<PoemSummary>, DataError.Local>

    suspend fun getPoemsByIds(ids: Set<Int>): Result<List<MyPoemSummary>, DataError.Local>

    suspend fun getPoemDetail(poemId: Int): Result<PoemDetail, DataError.Local>
}
