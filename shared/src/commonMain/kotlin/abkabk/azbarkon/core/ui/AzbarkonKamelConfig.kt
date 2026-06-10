package abkabk.azbarkon.core.ui

import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default

private const val POET_IMAGE_DISK_CACHE_BYTES = 50L * 1024 * 1024
private const val POET_IMAGE_MEMORY_CACHE_ENTRIES = 200

val AzbarkonKamelConfig =
    KamelConfig {
        takeFrom(KamelConfig.Default)
        imageBitmapCacheSize = POET_IMAGE_MEMORY_CACHE_ENTRIES
        httpFetcher {
            httpCache(POET_IMAGE_DISK_CACHE_BYTES)
        }
    }
