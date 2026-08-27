package abkabk.azbarkon.core.platform

expect class ShareManager {
    fun shareText(
        text: String,
        title: String?,
    )

    fun shareImage(
        imageBytes: ByteArray,
        title: String?,
    )

    fun shareFile(
        bytes: ByteArray,
        fileName: String,
        mimeType: String,
        title: String?,
    )
}
