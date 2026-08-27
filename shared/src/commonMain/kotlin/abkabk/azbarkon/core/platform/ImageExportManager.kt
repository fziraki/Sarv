package abkabk.azbarkon.core.platform

expect class ImageExportManager {
    suspend fun saveToGallery(
        imageBytes: ByteArray,
        fileName: String,
    ): Boolean
}
