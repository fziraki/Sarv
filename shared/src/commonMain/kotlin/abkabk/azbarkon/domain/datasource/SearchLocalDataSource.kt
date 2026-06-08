package abkabk.azbarkon.domain.datasource

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.SearchPage

interface SearchLocalDataSource {
    suspend fun getCatById(catId: Int): Result<CatNode, DataError.Local>

    suspend fun searchVerses(
        query: String,
        poetId: Int?,
        categoryIds: Set<Int>?,
        offset: Int,
        limit: Int,
    ): Result<SearchPage, DataError.Local>
}
