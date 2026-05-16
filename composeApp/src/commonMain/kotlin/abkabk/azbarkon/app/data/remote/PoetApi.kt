package abkabk.azbarkon.app.data.remote

import abkabk.azbarkon.app.data.dto.PoetDto

interface PoetApi {
    suspend fun getPoetList(): List<PoetDto>

}