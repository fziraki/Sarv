package abkabk.azbarkon

import abkabk.azbarkon.core.navigation.AzbarkonNavigation
import abkabk.azbarkon.core.ui.createAzbarkonImageLoader
import abkabk.azbarkon.domain.model.ThemeMode
import abkabk.azbarkon.domain.repository.UserPreferencesRepository
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.setSingletonImageLoaderFactory
import org.koin.compose.koinInject

@Composable
fun App(
    initialPoemId: Int? = null,
    openMemorizationPractice: Boolean = false,
) {
    setSingletonImageLoaderFactory { context ->
        createAzbarkonImageLoader(context)
    }

    val userPreferencesRepository: UserPreferencesRepository = koinInject()
    val themeMode by userPreferencesRepository.observeThemeMode().collectAsStateWithLifecycle(
        initialValue = userPreferencesRepository.getThemeMode(),
    )
    val darkTheme =
        when (themeMode) {
            ThemeMode.System -> isSystemInDarkTheme()
            ThemeMode.Light -> false
            ThemeMode.Dark -> true
        }

    val fontSizeScale by userPreferencesRepository.observeFontSizeScale().collectAsStateWithLifecycle(
        initialValue = 1f,
    )

    AzbarkonTheme(darkTheme = darkTheme, fontSizeScale = fontSizeScale) {
        AzbarkonNavigation(
            initialPoemId = initialPoemId,
            openMemorizationPractice = openMemorizationPractice,
        )
    }
}
