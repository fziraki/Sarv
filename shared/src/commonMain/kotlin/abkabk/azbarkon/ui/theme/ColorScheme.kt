package abkabk.azbarkon.ui.theme

import abkabk.azbarkon.core.designsystem.background
import abkabk.azbarkon.core.designsystem.darkBackground
import abkabk.azbarkon.core.designsystem.darkError
import abkabk.azbarkon.core.designsystem.darkOnBackground
import abkabk.azbarkon.core.designsystem.darkOnError
import abkabk.azbarkon.core.designsystem.darkOnPrimary
import abkabk.azbarkon.core.designsystem.darkOnSecondary
import abkabk.azbarkon.core.designsystem.darkOnSurface
import abkabk.azbarkon.core.designsystem.darkOnSurfaceVariant
import abkabk.azbarkon.core.designsystem.darkOnTertiary
import abkabk.azbarkon.core.designsystem.darkOutlineVariant
import abkabk.azbarkon.core.designsystem.darkPrimary
import abkabk.azbarkon.core.designsystem.darkSecondary
import abkabk.azbarkon.core.designsystem.darkSurface
import abkabk.azbarkon.core.designsystem.darkSurfaceVariant
import abkabk.azbarkon.core.designsystem.darkTertiary
import abkabk.azbarkon.core.designsystem.error
import abkabk.azbarkon.core.designsystem.onBackground
import abkabk.azbarkon.core.designsystem.onError
import abkabk.azbarkon.core.designsystem.onPrimary
import abkabk.azbarkon.core.designsystem.onSecondary
import abkabk.azbarkon.core.designsystem.onSurface
import abkabk.azbarkon.core.designsystem.onSurfaceVariant
import abkabk.azbarkon.core.designsystem.onTertiary
import abkabk.azbarkon.core.designsystem.outlineVariant
import abkabk.azbarkon.core.designsystem.primary
import abkabk.azbarkon.core.designsystem.secondary
import abkabk.azbarkon.core.designsystem.surface
import abkabk.azbarkon.core.designsystem.surfaceVariant
import abkabk.azbarkon.core.designsystem.tertiary
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val LightColorScheme =
    lightColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        tertiary = tertiary,
        onTertiary = onTertiary,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        error = error,
        onError = onError,
        outlineVariant = outlineVariant,
    )

val DarkColorScheme =
    darkColorScheme(
        primary = darkPrimary,
        onPrimary = darkOnPrimary,
        secondary = darkSecondary,
        onSecondary = darkOnSecondary,
        tertiary = darkTertiary,
        onTertiary = darkOnTertiary,
        background = darkBackground,
        onBackground = darkOnBackground,
        surface = darkSurface,
        onSurface = darkOnSurface,
        surfaceVariant = darkSurfaceVariant,
        onSurfaceVariant = darkOnSurfaceVariant,
        error = darkError,
        onError = darkOnError,
        outlineVariant = darkOutlineVariant,
    )
