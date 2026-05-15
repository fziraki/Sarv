package abkabk.azbarkon

import abkabk.azbarkon.app.core.navigation.AzbarkonNavigation
import abkabk.azbarkon.app.ui.theme.AzbarkonTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    AzbarkonTheme {
        AzbarkonNavigation()
    }
}