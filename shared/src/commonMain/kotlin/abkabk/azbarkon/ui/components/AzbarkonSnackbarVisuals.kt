package abkabk.azbarkon.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

data class AzbarkonSnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    val isSuccess: Boolean = false,
) : SnackbarVisuals