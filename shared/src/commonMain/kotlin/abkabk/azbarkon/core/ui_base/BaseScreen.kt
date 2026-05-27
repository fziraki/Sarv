package abkabk.azbarkon.core.ui_base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow

@Composable
fun <EFFECT> BaseScreen(
    screenState: UiScreenState,
    effectFlow: Flow<EFFECT>,
    onRetry: (() -> Unit)? = null,
    onEffect: suspend (EFFECT) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    LaunchedEffect(Unit) {
        effectFlow.collect(onEffect)
    }

    Box(
        modifier = modifier.fillMaxSize(),
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
