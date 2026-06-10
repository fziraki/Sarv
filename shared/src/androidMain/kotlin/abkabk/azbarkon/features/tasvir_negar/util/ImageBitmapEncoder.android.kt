package abkabk.azbarkon.features.tasvir_negar.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

actual fun ImageBitmap.encodeToPngBytes(): ByteArray? =
    runCatching {
        val outputStream = ByteArrayOutputStream()
        asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.toByteArray()
    }.getOrNull()
