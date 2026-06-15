package abkabk.azbarkon.core.ui

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.request.CachePolicy
import okio.FileSystem

private const val COIL_DISK_CACHE_BYTES = 50L * 1024 * 1024

fun createAzbarkonImageLoader(context: PlatformContext): ImageLoader =
    ImageLoader.Builder(context)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .diskCache {
            DiskCache.Builder()
                .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "coil_image_cache")
                .maxSizeBytes(COIL_DISK_CACHE_BYTES)
                .build()
        }
        .build()
