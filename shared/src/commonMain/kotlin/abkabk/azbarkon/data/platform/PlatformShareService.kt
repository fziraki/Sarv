package abkabk.azbarkon.data.platform

import abkabk.azbarkon.core.platform.ShareManager
import abkabk.azbarkon.domain.platform.ShareService

class PlatformShareService(
    private val shareManager: ShareManager,
) : ShareService {
    override fun shareText(
        text: String,
        title: String?,
    ) {
        shareManager.shareText(text = text, title = title)
    }

    override fun shareImage(
        imageBytes: ByteArray,
        title: String?,
    ) {
        shareManager.shareImage(imageBytes = imageBytes, title = title)
    }

    override fun shareFile(
        bytes: ByteArray,
        fileName: String,
        mimeType: String,
        title: String?,
    ) {
        shareManager.shareFile(bytes = bytes, fileName = fileName, mimeType = mimeType, title = title)
    }
}
