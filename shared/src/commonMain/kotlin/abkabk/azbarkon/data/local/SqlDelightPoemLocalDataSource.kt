package abkabk.azbarkon.data.local

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.data.mapper.toPoemSummary
import abkabk.azbarkon.domain.datasource.PoemLocalDataSource
import abkabk.azbarkon.domain.model.PoemSummary
import com.azbarkon.db.PoemQueries

class SqlDelightPoemLocalDataSource(
    private val poemQueries: PoemQueries,
) : PoemLocalDataSource {
    override suspend fun getPoemsByCatId(catId: Int): Result<List<PoemSummary>, DataError.Local> =
        try {
            Result.Success(
                poemQueries
                    .selectByCatId(cat_id = catId.toLong())
                    .executeAsList()
                    .map { it.toPoemSummary() },
            )
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
}
