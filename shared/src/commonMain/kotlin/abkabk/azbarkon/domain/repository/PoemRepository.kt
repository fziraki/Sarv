package abkabk.azbarkon.domain.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.PoemSummary

interface PoemRepository {
    suspend fun getPoemsByCatId(catId: Int): Result<List<PoemSummary>, DataError.Local>
}
