package abkabk.azbarkon.data.cache

import abkabk.azbarkon.domain.datasource.PoetImagePrefetcher
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CoilPoetImagePrefetcher(
    private val platformContext: PlatformContext,
) : PoetImagePrefetcher {
    private val prefetchScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun prefetch(imageUrls: List<String>) {
        val urlsToPrefetch =
            imageUrls
                .mapNotNull { url -> url.takeIf { it.isNotBlank() } }
                .distinct()
        if (urlsToPrefetch.isEmpty()) return

        prefetchScope.launch {
            val imageLoader = SingletonImageLoader.get(platformContext)
            urlsToPrefetch.forEach { url ->
                try {
                    imageLoader.enqueue(
                        ImageRequest.Builder(platformContext)
                            .data(url)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                    )
                } catch (_: Exception) {
                }
            }
        }
    }
}
