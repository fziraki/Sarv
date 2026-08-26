package abkabk.azbarkon

import abkabk.azbarkon.core.navigation.SarvNavigation
import abkabk.azbarkon.core.ui.createSarvImageLoader
import abkabk.azbarkon.domain.model.ThemeMode
import abkabk.azbarkon.domain.repository.UserPreferencesRepository
import abkabk.azbarkon.ui.theme.SarvTheme
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
        createSarvImageLoader(context)
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

    SarvTheme(darkTheme = darkTheme, fontSizeScale = fontSizeScale) {
        SarvNavigation(
            initialPoemId = initialPoemId,
            openMemorizationPractice = openMemorizationPractice,
        )
    }
}
