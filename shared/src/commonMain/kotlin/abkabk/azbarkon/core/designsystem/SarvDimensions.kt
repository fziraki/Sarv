package abkabk.azbarkon.core.designsystem

import abkabk.azbarkon.core.ui.LocalWindowSizeClass
import abkabk.azbarkon.core.ui.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.concurrent.Volatile

object SarvDimensions {
    @Volatile
    var scale: Float = 1f

    val dimen0: Dp get() = 0.dp
    val dimen1: Dp get() = (1 * scale).dp
    val dimen2: Dp get() = (2 * scale).dp
    val dimen3: Dp get() = (3 * scale).dp
    val dimen4: Dp get() = (4 * scale).dp
    val dimen5: Dp get() = (5 * scale).dp
    val dimen6: Dp get() = (6 * scale).dp
    val dimen8: Dp get() = (8 * scale).dp
    val dimen10: Dp get() = (10 * scale).dp
    val dimen12: Dp get() = (12 * scale).dp
    val dimen14: Dp get() = (14 * scale).dp
    val dimen16: Dp get() = (16 * scale).dp
    val dimen18: Dp get() = (18 * scale).dp
    val dimen20: Dp get() = (20 * scale).dp
    val dimen22: Dp get() = (22 * scale).dp
    val dimen24: Dp get() = (24 * scale).dp
    val dimen28: Dp get() = (28 * scale).dp
    val dimen32: Dp get() = (32 * scale).dp
    val dimen36: Dp get() = (36 * scale).dp
    val dimen40: Dp get() = (40 * scale).dp
    val dimen42: Dp get() = (42 * scale).dp
    val dimen44: Dp get() = (44 * scale).dp
    val dimen48: Dp get() = (48 * scale).dp
    val dimen52: Dp get() = (52 * scale).dp
    val dimen54: Dp get() = (54 * scale).dp
    val dimen56: Dp get() = (56 * scale).dp
    val dimen64: Dp get() = (64 * scale).dp
    val dimen68: Dp get() = (68 * scale).dp
    val dimen72: Dp get() = (72 * scale).dp
    val dimen80: Dp get() = (80 * scale).dp
    val dimen88: Dp get() = (88 * scale).dp
    val dimen92: Dp get() = (92 * scale).dp
    val dimen96: Dp get() = (96 * scale).dp
    val dimen120: Dp get() = (120 * scale).dp
    val dimen128: Dp get() = (128 * scale).dp
    val dimen160: Dp get() = (160 * scale).dp
    val dimen180: Dp get() = (180 * scale).dp
    val dimen200: Dp get() = (200 * scale).dp
    val dimen280: Dp get() = (280 * scale).dp
    val dimen300: Dp get() = (300 * scale).dp
}

@Composable
fun UpdateSarvDimensions() {
    val scale = deviceScale()
    SideEffect { SarvDimensions.scale = scale }
}

private const val EXPANDED_SCALE = 1.25f
private const val MEDIUM_SCALE = 1.125f
private const val COMPACT_SCALE = 1.0f

@Composable
private fun deviceScale(): Float {
    return when (LocalWindowSizeClass.current.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> EXPANDED_SCALE
        WindowWidthSizeClass.Medium -> MEDIUM_SCALE
        else -> COMPACT_SCALE
    }
}
