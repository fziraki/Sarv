package abkabk.azbarkon.data.remote

import abkabk.azbarkon.data.dto.PoetDto

interface PoetApi {
    suspend fun getPoetList(): List<PoetDto>

}