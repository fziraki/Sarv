package abkabk.azbarkon.domain.usecase

import abkabk.azbarkon.data.backup.UserBackupManager
import abkabk.azbarkon.domain.platform.ShareService

class ExportUserDataUseCase(
    private val userBackupManager: UserBackupManager,
    private val shareService: ShareService,
) {
    suspend operator fun invoke(): Boolean {
        return try {
            val json = userBackupManager.exportJson()
            shareService.shareFile(
                bytes = json.encodeToByteArray(),
                fileName = "azbarkon-backup.json",
                mimeType = "application/json",
                title = null,
            )
            true
        } catch (_: Exception) {
            false
        }
    }
}
