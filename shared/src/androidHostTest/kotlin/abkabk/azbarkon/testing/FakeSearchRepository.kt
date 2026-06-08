package abkabk.azbarkon.testing

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.SearchHit
import abkabk.azbarkon.domain.model.SearchPage
import abkabk.azbarkon.domain.repository.SearchRepository

class FakeSearchRepository : SearchRepository {
    var catsById: Map<Int, CatNode> = emptyMap()
    var searchPages: List<SearchHit> = emptyList()
    var shouldFailSearch: Boolean = false

    override suspend fun getCatById(catId: Int): Result<CatNode, DataError.Local> {
        val cat = catsById[catId] ?: return Result.Error(DataError.Local.NOT_FOUND)
        return Result.Success(cat)
    }

    override suspend fun searchVerses(
        query: String,
        poetId: Int?,
        categoryIds: Set<Int>?,
        offset: Int,
        limit: Int,
    ): Result<SearchPage, DataError.Local> {
        if (shouldFailSearch) {
            return Result.Error(DataError.Local.UNKNOWN)
        }

        val pageHits = searchPages.drop(offset).take(limit)
        return Result.Success(
            SearchPage(
                hits = pageHits,
                totalCount = searchPages.size,
            ),
        )
    }
}
