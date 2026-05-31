package abkabk.azbarkon.core.ui_base

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource

@Composable
fun UiText.asString(): String =
    when (this) {
        is UiText.DynamicString -> value
        is UiText.Resource -> stringResource(resource)
    }
