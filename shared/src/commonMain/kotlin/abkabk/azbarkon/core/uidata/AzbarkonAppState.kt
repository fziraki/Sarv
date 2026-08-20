package abkabk.azbarkon.core.uidata

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

@Composable
fun rememberAzbarkonAppState(): AzbarkonAppState = remember { AzbarkonAppState() }
