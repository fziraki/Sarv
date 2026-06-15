package abkabk.azbarkon

import abkabk.azbarkon.core.navigation.AzbarkonNavigation
import abkabk.azbarkon.core.ui.createAzbarkonImageLoader
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.setSingletonImageLoaderFactory

@Composable
@Preview
fun App(
    initialPoemId: Int? = null,
    openMemorizationPractice: Boolean = false,
) {
    setSingletonImageLoaderFactory { context ->
        createAzbarkonImageLoader(context)
    }
    AzbarkonTheme {
        AzbarkonNavigation(
            initialPoemId = initialPoemId,
            openMemorizationPractice = openMemorizationPractice,
        )
    }
}
