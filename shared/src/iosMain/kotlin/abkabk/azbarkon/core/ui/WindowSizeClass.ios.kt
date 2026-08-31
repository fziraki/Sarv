package abkabk.azbarkon.core.ui

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun calculateWindowSizeClass(): WindowSizeClass {
    val width = UIScreen.mainScreen.bounds.useContents { size.width.toInt() }
    return calculateWindowSizeClass(width)
}
