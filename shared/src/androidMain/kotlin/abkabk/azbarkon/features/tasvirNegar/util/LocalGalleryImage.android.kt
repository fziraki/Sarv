package abkabk.azbarkon.features.tasvirNegar.util

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
import abkabk.azbarkon.features.tasvirNegar.model.TasvirNegarColors
import java.io.File
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
actual fun LocalGalleryImage(
    uri: String,
    modifier: Modifier,
    contentScale: ContentScale,
) {
    var imageBitmap by remember(uri) { mutableStateOf<ImageBitmap?>(null) }
    val ioDispatcher: CoroutineDispatcher = koinInject()

    LaunchedEffect(uri) {
        val path = localImagePath(uri)
        imageBitmap =
            withContext(ioDispatcher) {
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

private const val ROTATE_90_DEGREES = 90f
private const val ROTATE_180_DEGREES = 180f
private const val ROTATE_270_DEGREES = 270f

private fun decodeOrientedBitmap(bitmap: Bitmap, path: String): Bitmap {
    val exif = ExifInterface(path)
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL,
    )
    if (orientation == ExifInterface.ORIENTATION_NORMAL) return bitmap

    val matrix = Matrix().apply {
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> postRotate(ROTATE_90_DEGREES)
            ExifInterface.ORIENTATION_ROTATE_180 -> postRotate(ROTATE_180_DEGREES)
            ExifInterface.ORIENTATION_ROTATE_270 -> postRotate(ROTATE_270_DEGREES)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> preScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                preScale(-1f, 1f)
                postRotate(ROTATE_90_DEGREES)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                preScale(1f, -1f)
                postRotate(ROTATE_90_DEGREES)
            }
        }
    }
    val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    if (result != bitmap) bitmap.recycle()
    return result
}
