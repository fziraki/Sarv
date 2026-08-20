package abkabk.azbarkon.core.uidata

import abkabk.azbarkon.ui.components.ShowSnackBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BaseScreen(
    screenState: UiScreenState,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
    ) {
        content()

        if (screenState == UiScreenState.Loading) {
            LoadingView()
        }

        if (screenState is UiScreenState.Error) {
            ShowSnackBar(
                key = screenState.key,
                message = screenState.message.asString(),
                isSuccess = screenState.isSuccess,
                hasRetry = screenState.retryable && onRetry != null,
                onRetry = onRetry,
            )
        }
    }
}

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}
