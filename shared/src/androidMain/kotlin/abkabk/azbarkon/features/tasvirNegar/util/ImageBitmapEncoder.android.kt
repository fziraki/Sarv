package abkabk.azbarkon.features.tasvirNegar.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

actual fun ImageBitmap.encodeToPngBytes(): ByteArray? =
    runCatching {
        val outputStream = ByteArrayOutputStream()
        asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, PNG_QUALITY, outputStream)
        outputStream.toByteArray()
    }.getOrNull()

private const val PNG_QUALITY = 100
