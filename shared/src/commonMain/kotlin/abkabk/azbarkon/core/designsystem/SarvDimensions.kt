package abkabk.azbarkon.core.designsystem

import abkabk.azbarkon.core.ui.LocalWindowSizeClass
import abkabk.azbarkon.core.ui.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
class SarvDimensions(
    val dimen0: Dp = 0.dp,
    val dimen1: Dp = 1.dp,
    val dimen2: Dp = 2.dp,
    val dimen3: Dp = 3.dp,
    val dimen4: Dp = 4.dp,
    val dimen5: Dp = 5.dp,
    val dimen6: Dp = 6.dp,
    val dimen8: Dp = 8.dp,
    val dimen10: Dp = 10.dp,
    val dimen12: Dp = 12.dp,
    val dimen14: Dp = 14.dp,
    val dimen16: Dp = 16.dp,
    val dimen18: Dp = 18.dp,
    val dimen20: Dp = 20.dp,
    val dimen22: Dp = 22.dp,
    val dimen24: Dp = 24.dp,
    val dimen28: Dp = 28.dp,
    val dimen32: Dp = 32.dp,
    val dimen36: Dp = 36.dp,
    val dimen40: Dp = 40.dp,
    val dimen42: Dp = 42.dp,
    val dimen44: Dp = 44.dp,
    val dimen48: Dp = 48.dp,
    val dimen52: Dp = 52.dp,
    val dimen54: Dp = 54.dp,
    val dimen56: Dp = 56.dp,
    val dimen64: Dp = 64.dp,
    val dimen68: Dp = 68.dp,
    val dimen72: Dp = 72.dp,
    val dimen80: Dp = 80.dp,
    val dimen88: Dp = 88.dp,
    val dimen92: Dp = 92.dp,
    val dimen96: Dp = 96.dp,
    val dimen120: Dp = 120.dp,
    val dimen128: Dp = 128.dp,
    val dimen160: Dp = 160.dp,
    val dimen180: Dp = 180.dp,
    val dimen200: Dp = 200.dp,
    val dimen280: Dp = 280.dp,
    val dimen300: Dp = 300.dp,
)

val LocalSarvDimensions = staticCompositionLocalOf {
    SarvDimensions()
}

@Composable
fun sarvDimensions(): SarvDimensions {
    val widthSizeClass = LocalWindowSizeClass.current.widthSizeClass
    val offset = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> 0.dp
        WindowWidthSizeClass.Medium -> 4.dp
        WindowWidthSizeClass.Expanded -> 8.dp
    }
    return SarvDimensions(
        dimen0 = 0.dp + offset,
        dimen1 = 1.dp + offset,
        dimen2 = 2.dp + offset,
        dimen3 = 3.dp + offset,
        dimen4 = 4.dp + offset,
        dimen5 = 5.dp + offset,
        dimen6 = 6.dp + offset,
        dimen8 = 8.dp + offset,
        dimen10 = 10.dp + offset,
        dimen12 = 12.dp + offset,
        dimen14 = 14.dp + offset,
        dimen16 = 16.dp + offset,
        dimen18 = 18.dp + offset,
        dimen20 = 20.dp + offset,
        dimen22 = 22.dp + offset,
        dimen24 = 24.dp + offset,
        dimen28 = 28.dp + offset,
        dimen32 = 32.dp + offset,
        dimen36 = 36.dp + offset,
        dimen40 = 40.dp + offset,
        dimen42 = 42.dp + offset,
        dimen44 = 44.dp + offset,
        dimen48 = 48.dp + offset,
        dimen52 = 52.dp + offset,
        dimen54 = 54.dp + offset,
        dimen56 = 56.dp + offset,
        dimen64 = 64.dp + offset,
        dimen68 = 68.dp + offset,
        dimen72 = 72.dp + offset,
        dimen80 = 80.dp + offset,
        dimen88 = 88.dp + offset,
        dimen92 = 92.dp + offset,
        dimen96 = 96.dp + offset,
        dimen120 = 120.dp + offset,
        dimen128 = 128.dp + offset,
        dimen160 = 160.dp + offset,
        dimen180 = 180.dp + offset,
        dimen200 = 200.dp + offset,
        dimen280 = 280.dp + offset,
        dimen300 = 300.dp + offset,
    )
}
