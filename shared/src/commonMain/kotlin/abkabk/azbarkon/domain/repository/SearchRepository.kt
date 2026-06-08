package abkabk.azbarkon.domain.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.SearchHit
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun getCatById(catId: Int): Result<CatNode, DataError.Local>

    fun searchVerses(
        query: String,
        poetId: Int?,
        categoryIds: Set<Int>?,
    ): Flow<PagingData<SearchHit>>
}
