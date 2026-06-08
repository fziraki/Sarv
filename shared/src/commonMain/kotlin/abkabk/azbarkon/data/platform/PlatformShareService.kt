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
}
