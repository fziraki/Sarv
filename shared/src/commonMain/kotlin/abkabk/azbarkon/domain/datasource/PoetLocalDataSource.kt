package abkabk.azbarkon.domain.datasource

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.Poet

interface PoetLocalDataSource {
    suspend fun getPoets(): Result<List<Poet>, DataError.Local>
}
