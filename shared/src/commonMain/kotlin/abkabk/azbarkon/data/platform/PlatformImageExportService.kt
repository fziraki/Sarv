package abkabk.azbarkon.data.platform

import abkabk.azbarkon.core.platform.ImageExportManager
import abkabk.azbarkon.domain.platform.ImageExportService

class PlatformImageExportService(
    private val imageExportManager: ImageExportManager,
) : ImageExportService {
    override suspend fun saveToGallery(
        imageBytes: ByteArray,
        fileName: String,
    ): Boolean = imageExportManager.saveToGallery(imageBytes = imageBytes, fileName = fileName)
}
