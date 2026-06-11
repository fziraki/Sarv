package abkabk.azbarkon.domain.platform

interface ShareService {
    fun shareText(
        text: String,
        title: String?,
    )

    fun shareImage(
        imageBytes: ByteArray,
        title: String?,
    )
}
