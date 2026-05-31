package abkabk.azbarkon.domain.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.Poet

interface PoetRepository {
    suspend fun getPoets(): Result<List<Poet>, DataError.Local>
}
