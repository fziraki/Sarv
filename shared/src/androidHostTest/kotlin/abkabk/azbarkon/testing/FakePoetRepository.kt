package abkabk.azbarkon.testing

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.domain.result.map
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetWithCategories
import abkabk.azbarkon.domain.model.PoetWithRootCategories
import abkabk.azbarkon.domain.repository.PoetRepository

class FakePoetRepository : PoetRepository {
    var poets: List<Poet> = emptyList()
    var poetsWithRootCategories: List<PoetWithRootCategories> = emptyList()
    var poetsWithCategories: List<PoetWithCategories> = emptyList()
    var shouldFail: Boolean = false

    override suspend fun getPoets(): Result<List<Poet>, DataError.Local> =
        if (shouldFail) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            getPoetsWithRootCategories().map { poetsWithRootCategories ->
                poetsWithRootCategories.map { it.poet }
            }
        }

    override suspend fun getPoetsWithRootCategories(): Result<List<PoetWithRootCategories>, DataError.Local> =
        if (shouldFail) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            Result.Success(
                poetsWithRootCategories.filter { it.rootCategories.isNotEmpty() },
            )
        }

    override suspend fun getPoetWithCategories(poetId: Int): Result<PoetWithCategories, DataError.Local> =
        if (shouldFail) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            val poetWithCategories =
                poetsWithCategories.find { it.poet.id == poetId }
                    ?: return Result.Error(DataError.Local.UNKNOWN)
            Result.Success(poetWithCategories)
        }
}
