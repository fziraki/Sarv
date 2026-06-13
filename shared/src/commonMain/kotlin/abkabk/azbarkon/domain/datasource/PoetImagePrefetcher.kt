package abkabk.azbarkon.domain.datasource

interface PoetImagePrefetcher {
    fun prefetch(imageUrls: List<String>)
}
