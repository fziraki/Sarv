package abkabk.azbarkon.features.tasvirNegar.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.unit.IntSize

@Composable
actual fun rememberCanvasCaptureModifier(
    onCaptureReady: (suspend () -> ByteArray?) -> Unit,
): Modifier {
    val graphicsLayer = rememberGraphicsLayer()
    val currentOnCaptureReady by rememberUpdatedState(onCaptureReady)

    LaunchedEffect(graphicsLayer) {
        currentOnCaptureReady {
            graphicsLayer.toImageBitmap().encodeToPngBytes()
        }
    }

    return Modifier.drawWithContent {
        graphicsLayer.record(
            density = this,
            layoutDirection = layoutDirection,
            size = IntSize(size.width.toInt(), size.height.toInt()),
        ) {
            this@drawWithContent.drawContent()
        }
        drawLayer(graphicsLayer)
    }
}
