package abkabk.azbarkon.domain.platform

interface ImageExportService {
    suspend fun saveToGallery(
        imageBytes: ByteArray,
        fileName: String,
    ): Boolean
}
