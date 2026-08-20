package abkabk.azbarkon.ui.components

import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.check_circle
import azbarkoncmp.shared.generated.resources.close
import azbarkoncmp.shared.generated.resources.retry
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShowSnackBar(
    message: String,
    key: Long?,
    modifier: Modifier = Modifier,
    hasRetry: Boolean = false,
    isSuccess: Boolean = false,
    onRetry: (() -> Unit)? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key) {
        snackbarHostState.showSnackbar(message)
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(top = 64.dp),
    ) {

        SnackbarHost(hostState = snackbarHostState) { data ->
            ShowSnackBarContent(
                data = data,
                hasRetry = hasRetry,
                isSuccess = isSuccess,
                onRetry = onRetry,
            )

        }

    }


}

@Composable
private fun ShowSnackBarContent(
    data: SnackbarData,
    hasRetry: Boolean,
    isSuccess: Boolean,
    onRetry: (() -> Unit)?,
) {
    Row(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            painter =
                painterResource(
                    if (isSuccess) Res.drawable.check_circle else Res.drawable.close,
                ),
            contentDescription = null,
            tint =
                if (isSuccess) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
        )

        Text(
            modifier = Modifier.weight(1f),
            text = data.visuals.message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        if (!isSuccess && hasRetry && onRetry != null) {
            Text(
                modifier = Modifier.clickable(onClick = onRetry),
                text = stringResource(Res.string.retry),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Preview
@Composable
private fun ShowSnackBarErrorPreview() {
    AzbarkonTheme {
        ShowSnackBar(
            key = 1L,
            message = "خطا در دریافت اطلاعات",
            onRetry = {  },
        )
    }
}

@Preview
@Composable
private fun ShowSnackBarSuccessPreview() {
    AzbarkonTheme {
        ShowSnackBar(
            key = 1L,
            message = "شعر با موفقیت ذخیره شد",
            isSuccess = true,
            onRetry = {  },
        )
    }
}

@Preview
@Composable
private fun ShowSnackBarRetryPreview() {
    AzbarkonTheme {
        ShowSnackBar(
            key = 1L,
            message = "خطا در دریافت اطلاعات",
            hasRetry = true,
            onRetry = {},
        )
    }
}
