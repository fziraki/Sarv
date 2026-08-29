package abkabk.azbarkon.core.uidata

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

class SarvAppState {
    var onProfileSettingsClick: (() -> Unit)? = null
    var notificationPermissionSheetShownThisLaunch: Boolean = false
}

val LocalSarvAppState =
    staticCompositionLocalOf<SarvAppState> {
        error("SarvAppState not provided")
    }

// ponytail: plain default keeps previews alive; the app always provides the real one in SarvNavigation
val LocalSnackbarHostState =
    staticCompositionLocalOf<SnackbarHostState> { SnackbarHostState() }

@Composable
fun rememberSarvAppState(): SarvAppState = remember { SarvAppState() }
