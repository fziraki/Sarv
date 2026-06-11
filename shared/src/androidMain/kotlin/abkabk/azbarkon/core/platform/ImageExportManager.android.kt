@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package abkabk.azbarkon.core.platform

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore

actual class ImageExportManager(
    private val context: Context,
) {
    actual suspend fun saveToGallery(
        imageBytes: ByteArray,
        fileName: String,
    ): Boolean =
        runCatching {
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val resolver = context.contentResolver
            val contentValues =
                ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Azbarkon")
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                }

            val uri =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return false

            resolver.openOutputStream(uri)?.use { output ->
                output.write(imageBytes)
            } ?: return false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            bitmap.recycle()
            true
        }.getOrDefault(false)
}
