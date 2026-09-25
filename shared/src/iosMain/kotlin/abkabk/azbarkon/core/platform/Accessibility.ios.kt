package abkabk.azbarkon.core.platform

import androidx.compose.runtime.Composable
import platform.UIKit.UIAccessibilityIsVoiceOverRunning

@Composable
actual fun isTouchExplorationEnabled(): Boolean = UIAccessibilityIsVoiceOverRunning()
