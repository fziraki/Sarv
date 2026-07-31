package abkabk.azbarkon.features.tasvirNegar.util

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.core.view.drawToBitmap
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val PNG_QUALITY = 100

@Composable
actual fun rememberCanvasCaptureModifier(
    onCaptureReady: (suspend () -> ByteArray?) -> Unit,
): Modifier {
    val view = LocalView.current
    var canvasBoundsInWindow by remember { mutableStateOf(Rect.Zero) }
    val currentBounds by rememberUpdatedState(canvasBoundsInWindow)
    val currentOnCaptureReady by rememberUpdatedState(onCaptureReady)

    LaunchedEffect(view) {
        currentOnCaptureReady {
            withContext(Dispatchers.Main) {
                runCatching {
                    val bounds = currentBounds
                    val cropWidth = bounds.width.toInt()
                    val cropHeight = bounds.height.toInt()
                    if (cropWidth <= 0 || cropHeight <= 0) {
                        return@withContext null
                    }

                    val rootLocation = IntArray(2)
                    view.getLocationInWindow(rootLocation)
                    val left = (bounds.left.toInt() - rootLocation[0]).coerceAtLeast(0)
                    val top = (bounds.top.toInt() - rootLocation[1]).coerceAtLeast(0)

                    val fullBitmap = view.drawToBitmap(Bitmap.Config.ARGB_8888)
                    val safeWidth = cropWidth.coerceAtMost(fullBitmap.width - left)
                    val safeHeight = cropHeight.coerceAtMost(fullBitmap.height - top)
                    if (safeWidth <= 0 || safeHeight <= 0) {
                        fullBitmap.recycle()
                        return@withContext null
                    }

                    val cropped =
                        Bitmap.createBitmap(
                            fullBitmap,
                            left,
                            top,
                            safeWidth,
                            safeHeight,
                        )
                    if (cropped !== fullBitmap) {
                        fullBitmap.recycle()
                    }

                    ByteArrayOutputStream().use { stream ->
                        cropped.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, stream)
                        cropped.recycle()
                        stream.toByteArray()
                    }
                }.getOrNull()
            }
        }
    }

    return Modifier.onGloballyPositioned { coordinates ->
        canvasBoundsInWindow = coordinates.boundsInWindow()
    }
}
