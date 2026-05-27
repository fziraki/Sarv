package abkabk.azbarkon.domain.repository

import abkabk.azbarkon.core.network.ApiResult
import abkabk.azbarkon.domain.model.Poet

interface PoetRepository {
    suspend fun getPoets(): ApiResult<List<Poet>>

    suspend fun getPoetsLocally(): List<Poet>
}
