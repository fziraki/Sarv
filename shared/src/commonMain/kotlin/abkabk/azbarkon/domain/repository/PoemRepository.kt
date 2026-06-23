package abkabk.azbarkon.domain.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.MyPoemSummary
import abkabk.azbarkon.domain.model.PoemAudioTrack
import abkabk.azbarkon.domain.model.PoemDetail
import abkabk.azbarkon.domain.model.PoemSummary
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface PoemRepository {
    fun poemsByCatId(catId: Int): Flow<PagingData<PoemSummary>>

    suspend fun getPoemsByIds(ids: Set<Int>): Result<List<MyPoemSummary>, DataError.Local>

    suspend fun getPoemDetail(poemId: Int): Result<PoemDetail, DataError.Local>

    suspend fun getPoemRecitations(poemId: Int): Result<List<PoemAudioTrack>, DataError.Network>
}
