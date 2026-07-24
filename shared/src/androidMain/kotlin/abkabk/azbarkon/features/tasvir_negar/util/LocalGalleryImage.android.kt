package abkabk.azbarkon.features.tasvir_negar.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import abkabk.azbarkon.features.tasvir_negar.model.TasvirNegarColors
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
actual fun LocalGalleryImage(
    uri: String,
    modifier: Modifier,
    contentScale: ContentScale,
) {
    var imageBitmap by remember(uri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uri) {
        val path = localImagePath(uri)
        imageBitmap =
            withContext(Dispatchers.IO) {
                runCatching {
                    val file = File(path)
                    if (!file.exists()) return@withContext null
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return@withContext null
                    decodeOrientedBitmap(bitmap, file.absolutePath).asImageBitmap()
                }.getOrNull()
            }
    }

    Box(modifier = modifier.background(TasvirNegarColors.canvasDefault)) {
        imageBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
            )
        }
    }
}

private fun decodeOrientedBitmap(bitmap: Bitmap, path: String): Bitmap {
    val exif = ExifInterface(path)
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL,
    )
    if (orientation == ExifInterface.ORIENTATION_NORMAL) return bitmap

    val matrix = Matrix().apply {
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> preScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                preScale(-1f, 1f)
                postRotate(90f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                preScale(1f, -1f)
                postRotate(90f)
            }
        }
    }
    val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    if (result != bitmap) bitmap.recycle()
    return result
}
