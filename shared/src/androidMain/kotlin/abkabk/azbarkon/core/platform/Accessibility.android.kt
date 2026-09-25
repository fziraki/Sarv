package abkabk.azbarkon.core.platform

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun isTouchExplorationEnabled(): Boolean {
    val context = LocalContext.current
    val manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
    return manager?.isTouchExplorationEnabled == true
}
