package abkabk.azbarkon.app.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(
    engine: HttpClientEngine,
    authProvider: AuthProvider
): HttpClient {

    return HttpClient(engine) {

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                }
            )
        }

        install(Logging) {
            level = LogLevel.BODY
        }

        install(AuthPlugin) {
            provider = authProvider
        }

        install(CachePlugin)

        defaultRequest {
            url("https://api.ganjoor.net/")
            header("Content-Type", "application/json")
        }
    }
}