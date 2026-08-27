package abkabk.azbarkon.core.network

import abkabk.azbarkon.core.util.Constants
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    private const val REQUEST_TIMEOUT_MILLIS = 30_000L

    fun create(
        engine: HttpClientEngine,
        authProvider: AuthProvider,
    ): HttpClient =
        HttpClient(engine) {
            install(HttpTimeout) {
                requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    },
                )
            }

            install(Logging) {
                logger =
                    object : Logger {
                        override fun log(message: String) {
                            Napier.d(tag = "Ktor", message = message)
                        }
                    }
                level = LogLevel.BODY
            }

            install(AuthPlugin) {
                provider = authProvider
            }

            defaultRequest {
                url(Constants.BASE_URL)
                header("Content-Type", "application/json")
            }
        }
}
