package abkabk.azbarkon.app.data.remote

import abkabk.azbarkon.app.data.dto.PoetDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

class PoetApiImpl(
    private val client: HttpClient
) : PoetApi {

    override suspend fun getPoetList(): List<PoetDto> {
        return client.get("api/ganjoor/poets") {
            header("isAuthorizable", "false")
            header("cacheSeconds", 60 * 60 * 24)
        }.body()
    }

}