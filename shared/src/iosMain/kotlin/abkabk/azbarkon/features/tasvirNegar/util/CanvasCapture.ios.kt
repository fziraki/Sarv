package abkabk.azbarkon.features.tasvirNegar.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.layer.rememberGraphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toIntSize

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

    return Modifier
        .onSizeChanged { graphicsLayer.size = it }
        .drawWithContent {
            graphicsLayer.record(
                density = this,
                layoutDirection = layoutDirection,
                size = size.toIntSize(),
            ) {
                this@drawWithContent.drawContent()
            }
            drawLayer(graphicsLayer)
        }
}
