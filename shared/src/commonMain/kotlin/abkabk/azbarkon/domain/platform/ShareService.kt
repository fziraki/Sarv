package abkabk.azbarkon.domain.platform

interface ShareService {
    fun shareText(
        text: String,
        title: String?,
    )
}
