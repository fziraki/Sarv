package abkabk.azbarkon.features.profile.widget

import androidx.compose.runtime.Composable

// No WidgetKit target yet, so iOS has no home-screen widget to add.
@Composable
actual fun rememberWidgetPickerLauncher(): (() -> Unit)? = null
