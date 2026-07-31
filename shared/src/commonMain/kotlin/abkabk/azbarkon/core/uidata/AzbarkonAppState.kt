package abkabk.azbarkon.core.uidata

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AzbarkonAppState(
    val snackbarHostState: SnackbarHostState,
    private val scope: CoroutineScope,
) {
    var onProfileSettingsClick: (() -> Unit)? = null

    fun showSnackbar(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
}

val LocalAzbarkonAppState =
    staticCompositionLocalOf<AzbarkonAppState> {
        error("AzbarkonAppState not provided")
    }

@Composable
fun rememberAzbarkonAppState(): AzbarkonAppState {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    return remember(snackbarHostState, scope) {
        AzbarkonAppState(
            snackbarHostState = snackbarHostState,
            scope = scope,
        )
    }
}
