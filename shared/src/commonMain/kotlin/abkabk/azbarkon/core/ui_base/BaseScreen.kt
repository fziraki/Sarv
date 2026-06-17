package abkabk.azbarkon.core.ui_base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BaseScreen(
    screenState: UiScreenState,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
    ) {
        content()

        when (screenState) {
            UiScreenState.Loading -> {
                LoadingView()
            }

            is UiScreenState.Error -> {
                ErrorView(
                    message = screenState.message.asString(),
                    showRetry = screenState.retryable,
                    onRetry = onRetry,
                )
            }

            else -> Unit
        }
    }
}

@Composable
fun ErrorView(
    message: String,
    showRetry: Boolean,
    onRetry: (() -> Unit)?,
) {
}

@Composable
fun LoadingView() {
}
