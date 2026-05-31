package abkabk.azbarkon.data.local

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.data.mapper.toPoet
import abkabk.azbarkon.domain.datasource.PoetLocalDataSource
import abkabk.azbarkon.domain.model.Poet
import com.azbarkon.db.PoetQueries

class SqlDelightPoetLocalDataSource(
    private val queries: PoetQueries,
) : PoetLocalDataSource {
    override suspend fun getPoets(): Result<List<Poet>, DataError.Local> =
        try {
            Result.Success(
                queries
                    .selectAll()
                    .executeAsList()
                    .map { it.toPoet() },
            )
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
}
