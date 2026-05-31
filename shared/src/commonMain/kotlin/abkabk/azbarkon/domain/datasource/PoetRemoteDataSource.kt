package abkabk.azbarkon.domain.datasource

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.Poet

interface PoetRemoteDataSource {
    suspend fun fetchPoets(): Result<List<Poet>, DataError.Network>
}
