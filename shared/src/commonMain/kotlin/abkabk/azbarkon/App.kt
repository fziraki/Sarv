package abkabk.azbarkon

import abkabk.azbarkon.core.navigation.AzbarkonNavigation
import abkabk.azbarkon.core.ui.AzbarkonKamelConfig
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import io.kamel.image.config.LocalKamelConfig

@Composable
@Preview
fun App(initialPoemId: Int? = null) {
    CompositionLocalProvider(LocalKamelConfig provides AzbarkonKamelConfig) {
        AzbarkonTheme {
            AzbarkonNavigation(initialPoemId = initialPoemId)
        }
    }
}
