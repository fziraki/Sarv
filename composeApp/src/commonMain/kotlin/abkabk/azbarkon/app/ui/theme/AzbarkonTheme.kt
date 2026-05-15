package abkabk.azbarkon.app.ui.theme

import abkabk.azbarkon.app.core.designsystem.AzbarkonTypography
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme

@Composable
fun AzbarkonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AzbarkonTypography,
        content = content
    )
}