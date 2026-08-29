package abkabk.azbarkon.testing

import abkabk.azbarkon.domain.platform.ShareService

class FakeShareService : ShareService {
    var lastSharedText: String? = null
    var lastSharedTitle: String? = null
    var lastSharedImageBytes: ByteArray? = null
    var lastSharedFileBytes: ByteArray? = null
    var lastSharedFileName: String? = null
    var lastSharedFileMimeType: String? = null

    override fun shareText(
        text: String,
        title: String?,
    ) {
        lastSharedText = text
        lastSharedTitle = title
    }

    override fun shareImage(
        imageBytes: ByteArray,
        title: String?,
    ) {
        lastSharedImageBytes = imageBytes
        lastSharedTitle = title
    }

    override fun shareFile(
        bytes: ByteArray,
        fileName: String,
        mimeType: String,
        title: String?,
    ) {
        lastSharedFileBytes = bytes
        lastSharedFileName = fileName
        lastSharedFileMimeType = mimeType
        lastSharedTitle = title
    }
}
