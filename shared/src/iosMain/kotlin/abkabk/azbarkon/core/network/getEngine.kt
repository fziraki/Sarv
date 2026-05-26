package abkabk.azbarkon.core.network

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

actual fun getEngine(): HttpClientEngine {
    return Darwin.create()
}