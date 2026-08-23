package abkabk.azbarkon.core.uidata

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

class AzbarkonAppState {
    var onProfileSettingsClick: (() -> Unit)? = null
    var notificationPermissionSheetShownThisLaunch: Boolean = false
}

@Suppress("CompositionLocalAllowlist")
val LocalAzbarkonAppState =
    staticCompositionLocalOf<AzbarkonAppState> {
        error("AzbarkonAppState not provided")
    }

// ponytail: plain default keeps previews alive; the app always provides the real one in AzbarkonNavigation
@Suppress("CompositionLocalAllowlist")
val LocalSnackbarHostState =
    staticCompositionLocalOf<SnackbarHostState> { SnackbarHostState() }

@Composable
fun rememberAzbarkonAppState(): AzbarkonAppState = remember { AzbarkonAppState() }
