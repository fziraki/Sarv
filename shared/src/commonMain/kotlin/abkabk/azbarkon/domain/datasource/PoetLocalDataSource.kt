package abkabk.azbarkon.domain.datasource

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetWithCategories
import abkabk.azbarkon.domain.model.PoetWithRootCategories

interface PoetLocalDataSource {
    suspend fun getPoets(): Result<List<Poet>, DataError.Local>

    suspend fun getPoetsWithRootCategories(): Result<List<PoetWithRootCategories>, DataError.Local>

    suspend fun getPoetWithCategories(poetId: Int): Result<PoetWithCategories, DataError.Local>
}
