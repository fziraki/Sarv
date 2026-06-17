package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.domain.datasource.PoetImagePrefetcher
import abkabk.azbarkon.domain.datasource.PoetLocalDataSource
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetWithCategories
import abkabk.azbarkon.domain.model.PoetWithRootCategories
import abkabk.azbarkon.domain.repository.PoetRepository

class OfflineFirstPoetRepository(
    private val localDataSource: PoetLocalDataSource,
    private val poetImagePrefetcher: PoetImagePrefetcher,
) : PoetRepository {
    override suspend fun getPoets(): Result<List<Poet>, DataError.Local> =
        localDataSource.getPoets().also { result ->
            result.onSuccess { poets ->
                poetImagePrefetcher.prefetch(poets.mapNotNull { it.imageUrl })
            }
        }

    override suspend fun getPoetsWithRootCategories(): Result<List<PoetWithRootCategories>, DataError.Local> =
        localDataSource.getPoetsWithRootCategories().also { result ->
            result.onSuccess { poets ->
                poetImagePrefetcher.prefetch(poets.mapNotNull { it.poet.imageUrl })
            }
        }

    override suspend fun getPoetWithCategories(poetId: Int): Result<PoetWithCategories, DataError.Local> =
        localDataSource.getPoetWithCategories(poetId).also { result ->
            result.onSuccess { poetWithCategories ->
                poetWithCategories.poet.imageUrl?.let { imageUrl ->
                    poetImagePrefetcher.prefetch(listOf(imageUrl))
                }
            }
        }
}
