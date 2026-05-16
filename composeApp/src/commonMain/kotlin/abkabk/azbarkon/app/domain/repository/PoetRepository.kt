package abkabk.azbarkon.app.domain.repository

import abkabk.azbarkon.app.core.network.ApiResult
import abkabk.azbarkon.app.domain.model.Poet

interface PoetRepository {
    suspend fun getPoets(): ApiResult<List<Poet>>
}