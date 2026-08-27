package abkabk.azbarkon.data.local

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.domain.result.map
import abkabk.azbarkon.data.mapper.buildPoetCategoryTree
import abkabk.azbarkon.data.mapper.toCatNode
import abkabk.azbarkon.data.mapper.toPoet
import abkabk.azbarkon.domain.datasource.PoetLocalDataSource
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetWithCategories
import abkabk.azbarkon.domain.model.PoetWithRootCategories
import com.sarv.db.CatQueries
import com.sarv.db.PoetQueries
import io.github.aakira.napier.Napier

class SqlDelightPoetLocalDataSource(
    private val poetQueries: PoetQueries,
    private val catQueries: CatQueries,
) : PoetLocalDataSource {
    override suspend fun getPoets(): Result<List<Poet>, DataError.Local> =
        getPoetsWithRootCategories().map { poetsWithRootCategories ->
            poetsWithRootCategories.map { it.poet }
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getPoetsWithRootCategories(): Result<List<PoetWithRootCategories>, DataError.Local> =
        try {
            val poets =
                poetQueries
                    .selectAllWithCatUrl()
                    .executeAsList()
                    .map { it.toPoet() }

            Result.Success(
                poets.map { poet -> buildPoetWithRootCategories(poet) },
            )
        } catch (e: Exception) {
            Napier.e(message = "getPoetsWithRootCategories failed: ${e.message}", throwable = e, tag = "PoetDb")
            Result.Error(DataError.Local.QUERY_FAILED)
        }

    override suspend fun getPoetWithCategories(poetId: Int): Result<PoetWithCategories, DataError.Local> =
        try {
            val poetRow =
                poetQueries
                    .selectByIdWithCatUrl(poetId.toLong())
                    .executeAsOneOrNull()
                    ?: return Result.Error(DataError.Local.NOT_FOUND)

            Result.Success(buildPoetWithCategories(poetRow.toPoet()))
        } catch (e: Exception) {
            Napier.e("getPoetWithCategories failed for poetId=$poetId", e)
            Result.Error(DataError.Local.QUERY_FAILED)
        }

    private fun buildPoetWithRootCategories(poet: Poet): PoetWithRootCategories {
        val poetId = poet.id ?: return PoetWithRootCategories(poet = poet, rootCategories = emptyList())
        val rootCatId = poet.rootCatId ?: return PoetWithRootCategories(poet = poet, rootCategories = emptyList())

        val rootCategories =
            catQueries
                .selectChildrenByParentId(
                    parent_id = rootCatId.toLong(),
                    poet_id = poetId.toLong(),
                ).executeAsList()
                .map { it.toCatNode() }

        val allCategories =
            catQueries
                .selectAllByPoetId(poet_id = poetId.toLong())
                .executeAsList()
                .map { it.toCatNode() }

        return PoetWithRootCategories(poet = poet, rootCategories = rootCategories, allCategories = allCategories)
    }

    private fun buildPoetWithCategories(poet: Poet): PoetWithCategories {
        val poetId = poet.id ?: return PoetWithCategories(poet = poet, categories = emptyList())
        val rootCatId = poet.rootCatId ?: return PoetWithCategories(poet = poet, categories = emptyList())

        val allCategories =
            catQueries
                .selectAllByPoetId(poet_id = poetId.toLong())
                .executeAsList()
                .map { it.toCatNode() }

        val categories = buildPoetCategoryTree(rootCatId, allCategories)
        return PoetWithCategories(poet = poet, categories = categories)
    }
}
