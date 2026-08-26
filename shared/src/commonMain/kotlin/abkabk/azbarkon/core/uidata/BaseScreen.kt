package abkabk.azbarkon.core.uidata

import abkabk.azbarkon.ui.components.SarvSnackbarVisuals
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import sarv.shared.generated.resources.Res
import sarv.shared.generated.resources.retry
import org.jetbrains.compose.resources.stringResource

@Composable
fun BaseScreen(
    screenState: UiScreenState,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val snackbarHostState = LocalSnackbarHostState.current

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
            val message = screenState.message.asString()
            val actionLabel =
                if (screenState.retryable && onRetry != null) {
                    stringResource(Res.string.retry)
                } else {
                    null
                }
            LaunchedEffect(screenState.key) {
                val result =
                    snackbarHostState.showSnackbar(
                        SarvSnackbarVisuals(
                            message = message,
                            actionLabel = actionLabel,
                            isSuccess = screenState.isSuccess,
                        ),
                    )
                if (result == SnackbarResult.ActionPerformed) {
                    onRetry?.invoke()
                }
            }
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
