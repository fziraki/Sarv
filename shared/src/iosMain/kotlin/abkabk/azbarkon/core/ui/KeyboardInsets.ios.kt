package abkabk.azbarkon.core.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity

@Composable
private fun keyboardLiftPx(): Int {
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val navigationBottom = WindowInsets.navigationBars.getBottom(density)
    return (imeBottom - navigationBottom).coerceAtLeast(0)
}

@Composable
actual fun rememberKeyboardLiftPx(): Int = keyboardLiftPx()

@Composable
actual fun Modifier.keyboardAboveIme(): Modifier =
    composed {
        val density = LocalDensity.current
        val lift = keyboardLiftPx()
        padding(bottom = with(density) { lift.toDp() })
    }
