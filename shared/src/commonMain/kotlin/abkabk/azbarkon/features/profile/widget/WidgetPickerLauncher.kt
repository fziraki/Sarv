package abkabk.azbarkon.features.profile.widget

import androidx.compose.runtime.Composable

@Composable
// Returns null on platforms without a home-screen widget (iOS has no WidgetKit target yet).
expect fun rememberWidgetPickerLauncher(): (() -> Unit)?
