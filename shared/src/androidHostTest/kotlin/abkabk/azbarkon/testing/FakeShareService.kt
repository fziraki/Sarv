package abkabk.azbarkon.testing

import abkabk.azbarkon.domain.platform.ShareService

class FakeShareService : ShareService {
    var lastSharedText: String? = null
    var lastSharedTitle: String? = null
    var lastSharedImageBytes: ByteArray? = null

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
}
