package abkabk.azbarkon.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Lifts content above the software keyboard without double-counting the navigation bar inset.
 * Apply on the bottom input bar.
 */
expect fun Modifier.keyboardAboveIme(): Modifier

/** Current keyboard lift in pixels (IME minus navigation bar). Recomposes as the keyboard animates. */
@Composable
expect fun rememberKeyboardLiftPx(): Int
