package abkabk.azbarkon.core.network

import io.ktor.client.plugins.api.createClientPlugin

class CachePluginConfig

val CachePlugin =
    createClientPlugin("CachePlugin", ::CachePluginConfig) {

        onRequest { request, _ ->

            val cacheHeader = request.headers["cacheSeconds"]
            request.headers.remove("cacheSeconds")

            cacheHeader?.let {
                request.headers.append(
                    "Cache-Control",
                    "max-age=$it",
                )
            }
        }
    }
