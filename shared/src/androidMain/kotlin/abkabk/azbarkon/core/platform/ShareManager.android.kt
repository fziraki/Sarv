@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package abkabk.azbarkon.core.platform

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

actual class ShareManager(
    private val context: Context,
) {
    actual fun shareText(
        text: String,
        title: String?,
    ) {
        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                title?.let { putExtra(Intent.EXTRA_TITLE, it) }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        context.startActivity(Intent.createChooser(intent, title).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    actual fun shareImage(
        imageBytes: ByteArray,
        title: String?,
    ) {
        val cacheDir = File(context.cacheDir, "shared_images").apply { mkdirs() }
        val imageFile = File(cacheDir, "azbarkon_share_${System.currentTimeMillis()}.png")
        imageFile.writeBytes(imageBytes)

        val uri =
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile,
            )

        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                title?.let { putExtra(Intent.EXTRA_TITLE, it) }
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        context.startActivity(Intent.createChooser(intent, title).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
