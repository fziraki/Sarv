package abkabk.azbarkon.features.tasvirNegar.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun rememberCanvasCaptureModifier(
    onCaptureReady: (suspend () -> ByteArray?) -> Unit,
): Modifier
