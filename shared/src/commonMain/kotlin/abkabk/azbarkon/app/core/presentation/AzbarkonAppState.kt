package abkabk.azbarkon.app.core.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class AzbarkonAppState(
    val snackbarHostState: SnackbarHostState
) {

    suspend fun showSnackbar(
        message: String
    ) {
        snackbarHostState.showSnackbar(message)
    }
}

@Composable
fun rememberAzbarkonAppState(): AzbarkonAppState {

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    return remember {
        AzbarkonAppState(
            snackbarHostState
        )
    }
}