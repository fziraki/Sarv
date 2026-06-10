package abkabk.azbarkon.features.tasvir_negar.util

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.drawToBitmap
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
actual fun rememberCanvasCaptureModifier(
    onCaptureReady: (suspend () -> ByteArray?) -> Unit,
): Modifier {
    val view = LocalView.current

    LaunchedEffect(view) {
        onCaptureReady {
            withContext(Dispatchers.Main) {
                runCatching {
                    val bitmap = view.drawToBitmap(Bitmap.Config.ARGB_8888)
                    ByteArrayOutputStream().use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        stream.toByteArray()
                    }
                }.getOrNull()
            }
        }
    }

    return Modifier
}
