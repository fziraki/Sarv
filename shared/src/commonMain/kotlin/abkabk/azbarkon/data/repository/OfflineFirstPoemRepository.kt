package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.paging.DEFAULT_PAGING_CONFIG
import abkabk.azbarkon.data.paging.PoemListPagingSource
import abkabk.azbarkon.domain.datasource.PoemLocalDataSource
import abkabk.azbarkon.domain.datasource.PoemRemoteDataSource
import abkabk.azbarkon.domain.model.MyPoemSummary
import abkabk.azbarkon.domain.model.PoemAudioTrack
import abkabk.azbarkon.domain.model.PoemDetail
import abkabk.azbarkon.domain.model.PoemSummary
import abkabk.azbarkon.domain.repository.PoemRepository
import androidx.paging.Pager
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class OfflineFirstPoemRepository(
    private val localDataSource: PoemLocalDataSource,
    private val remoteDataSource: PoemRemoteDataSource
) : PoemRepository {
    override fun poemsByCatId(catId: Int): Flow<PagingData<PoemSummary>> =
        Pager(
            config = DEFAULT_PAGING_CONFIG,
            pagingSourceFactory = { PoemListPagingSource(localDataSource, catId) },
        ).flow

    override suspend fun getPoemsByIds(ids: Set<Int>): Result<List<MyPoemSummary>, DataError.Local> =
        localDataSource.getPoemsByIds(ids)

    override suspend fun getPoemDetail(poemId: Int): Result<PoemDetail, DataError.Local> =
        localDataSource.getPoemDetail(poemId)

    override suspend fun getPoemRecitations(poemId: Int): Result<List<PoemAudioTrack>, DataError.Network> =
        remoteDataSource.getPoemRecitations(poemId)
}
