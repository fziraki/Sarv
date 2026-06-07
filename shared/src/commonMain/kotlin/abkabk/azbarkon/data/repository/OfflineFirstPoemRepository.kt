package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.datasource.PoemLocalDataSource
import abkabk.azbarkon.domain.model.PoemSummary
import abkabk.azbarkon.domain.repository.PoemRepository

class OfflineFirstPoemRepository(
    private val localDataSource: PoemLocalDataSource,
) : PoemRepository {
    override suspend fun getPoemsByCatId(catId: Int): Result<List<PoemSummary>, DataError.Local> =
        localDataSource.getPoemsByCatId(catId)
}
