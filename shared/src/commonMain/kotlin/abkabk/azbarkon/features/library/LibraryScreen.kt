package abkabk.azbarkon.features.library

import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.library
import azbarkoncmp.shared.generated.resources.library_coming_soon
import org.jetbrains.compose.resources.stringResource

@Composable
fun LibraryRoot() {
    LibraryScreen()
}

@Composable
fun LibraryScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "${stringResource(Res.string.library)} — ${stringResource(Res.string.library_coming_soon)}",
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Preview
@Composable
private fun LibraryScreenPreview() {
    AzbarkonTheme {
        LibraryScreen()
    }
}
