package abkabk.azbarkon.domain.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetWithWorks

interface PoetRepository {
    suspend fun getPoets(): Result<List<Poet>, DataError.Local>

    suspend fun getPoetsWithWorks(): Result<List<PoetWithWorks>, DataError.Local>

    suspend fun getPoetWithWorks(poetId: Int): Result<PoetWithWorks, DataError.Local>
}
