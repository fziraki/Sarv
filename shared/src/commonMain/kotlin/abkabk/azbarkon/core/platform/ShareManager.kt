@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

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
