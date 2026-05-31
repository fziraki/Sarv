package abkabk.azbarkon.data.local

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.data.mapper.resolvePoetWorks
import abkabk.azbarkon.data.mapper.toCatNode
import abkabk.azbarkon.data.mapper.toPoet
import abkabk.azbarkon.domain.datasource.PoetLocalDataSource
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetWithWorks
import com.azbarkon.db.CatQueries
import com.azbarkon.db.PoetQueries

class SqlDelightPoetLocalDataSource(
    private val poetQueries: PoetQueries,
    private val catQueries: CatQueries,
) : PoetLocalDataSource {
    override suspend fun getPoets(): Result<List<Poet>, DataError.Local> =
        try {
            Result.Success(
                poetQueries
                    .selectAllWithCatUrl()
                    .executeAsList()
                    .map { it.toPoet() },
            )
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun getPoetsWithWorks(): Result<List<PoetWithWorks>, DataError.Local> =
        try {
            val poets =
                poetQueries
                    .selectAllWithCatUrl()
                    .executeAsList()
                    .map { it.toPoet() }

            Result.Success(
                poets.map { poet -> buildPoetWithWorks(poet) },
            )
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun getPoetWithWorks(poetId: Int): Result<PoetWithWorks, DataError.Local> =
        try {
            val poetRow =
                poetQueries
                    .selectByIdWithCatUrl(poetId.toLong())
                    .executeAsOneOrNull()
                    ?: return Result.Error(DataError.Local.UNKNOWN)

            Result.Success(buildPoetWithWorks(poetRow.toPoet()))
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    private fun buildPoetWithWorks(poet: Poet): PoetWithWorks {
        val poetId = poet.id ?: return PoetWithWorks(poet = poet, works = emptyList())
        val rootCatId = poet.rootCatId ?: return PoetWithWorks(poet = poet, works = emptyList())

        val rootChildren =
            catQueries
                .selectChildrenByParentId(
                    parent_id = rootCatId.toLong(),
                    poet_id = poetId.toLong(),
                ).executeAsList()
                .map { it.toCatNode() }

        val works = resolvePoetWorks(poet, rootChildren)
        return PoetWithWorks(poet = poet, works = works)
    }
}
