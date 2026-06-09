package abkabk.azbarkon

import abkabk.azbarkon.core.navigation.AzbarkonNavigation
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(initialPoemId: Int? = null) {
    AzbarkonTheme {
        AzbarkonNavigation(initialPoemId = initialPoemId)
    }
}
