package abkabk.azbarkon.app.core.network

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

actual fun getEngine(): HttpClientEngine {
    return OkHttp.create()
}