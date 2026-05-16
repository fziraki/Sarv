package abkabk.azbarkon.app.ui.theme

import abkabk.azbarkon.app.core.designsystem.background
import abkabk.azbarkon.app.core.designsystem.darkBackground
import abkabk.azbarkon.app.core.designsystem.darkError
import abkabk.azbarkon.app.core.designsystem.darkErrorContainer
import abkabk.azbarkon.app.core.designsystem.darkInverseOnSurface
import abkabk.azbarkon.app.core.designsystem.darkInversePrimary
import abkabk.azbarkon.app.core.designsystem.darkInverseSurface
import abkabk.azbarkon.app.core.designsystem.darkOnBackground
import abkabk.azbarkon.app.core.designsystem.darkOnError
import abkabk.azbarkon.app.core.designsystem.darkOnPrimary
import abkabk.azbarkon.app.core.designsystem.darkOnPrimaryContainer
import abkabk.azbarkon.app.core.designsystem.darkOnSecondary
import abkabk.azbarkon.app.core.designsystem.darkOnSecondaryContainer
import abkabk.azbarkon.app.core.designsystem.darkOnSurface
import abkabk.azbarkon.app.core.designsystem.darkOnSurfaceVariant
import abkabk.azbarkon.app.core.designsystem.darkOnTertiary
import abkabk.azbarkon.app.core.designsystem.darkOnTertiaryContainer
import abkabk.azbarkon.app.core.designsystem.darkOutline
import abkabk.azbarkon.app.core.designsystem.darkOutlineVariant
import abkabk.azbarkon.app.core.designsystem.darkPrimary
import abkabk.azbarkon.app.core.designsystem.darkPrimaryContainer
import abkabk.azbarkon.app.core.designsystem.darkSecondary
import abkabk.azbarkon.app.core.designsystem.darkSecondaryContainer
import abkabk.azbarkon.app.core.designsystem.darkSurface
import abkabk.azbarkon.app.core.designsystem.darkSurfaceBright
import abkabk.azbarkon.app.core.designsystem.darkSurfaceContainer
import abkabk.azbarkon.app.core.designsystem.darkSurfaceContainerHigh
import abkabk.azbarkon.app.core.designsystem.darkSurfaceContainerHighest
import abkabk.azbarkon.app.core.designsystem.darkSurfaceContainerLow
import abkabk.azbarkon.app.core.designsystem.darkSurfaceContainerLowest
import abkabk.azbarkon.app.core.designsystem.darkSurfaceDim
import abkabk.azbarkon.app.core.designsystem.darkSurfaceVariant
import abkabk.azbarkon.app.core.designsystem.darkTertiary
import abkabk.azbarkon.app.core.designsystem.darkTertiaryContainer
import abkabk.azbarkon.app.core.designsystem.error
import abkabk.azbarkon.app.core.designsystem.errorContainer
import abkabk.azbarkon.app.core.designsystem.inverseOnSurface
import abkabk.azbarkon.app.core.designsystem.inversePrimary
import abkabk.azbarkon.app.core.designsystem.inverseSurface
import abkabk.azbarkon.app.core.designsystem.onBackground
import abkabk.azbarkon.app.core.designsystem.onError
import abkabk.azbarkon.app.core.designsystem.onErrorContainer
import abkabk.azbarkon.app.core.designsystem.onPrimary
import abkabk.azbarkon.app.core.designsystem.onPrimaryContainer
import abkabk.azbarkon.app.core.designsystem.onPrimaryFixed
import abkabk.azbarkon.app.core.designsystem.onPrimaryFixedVariant
import abkabk.azbarkon.app.core.designsystem.onSecondary
import abkabk.azbarkon.app.core.designsystem.onSecondaryContainer
import abkabk.azbarkon.app.core.designsystem.onSecondaryFixed
import abkabk.azbarkon.app.core.designsystem.onSecondaryFixedVariant
import abkabk.azbarkon.app.core.designsystem.onSurface
import abkabk.azbarkon.app.core.designsystem.onSurfaceVariant
import abkabk.azbarkon.app.core.designsystem.onTertiary
import abkabk.azbarkon.app.core.designsystem.onTertiaryContainer
import abkabk.azbarkon.app.core.designsystem.onTertiaryFixed
import abkabk.azbarkon.app.core.designsystem.onTertiaryFixedVariant
import abkabk.azbarkon.app.core.designsystem.outline
import abkabk.azbarkon.app.core.designsystem.outlineVariant
import abkabk.azbarkon.app.core.designsystem.primary
import abkabk.azbarkon.app.core.designsystem.primaryContainer
import abkabk.azbarkon.app.core.designsystem.primaryFixed
import abkabk.azbarkon.app.core.designsystem.primaryFixedDim
import abkabk.azbarkon.app.core.designsystem.scrim
import abkabk.azbarkon.app.core.designsystem.secondary
import abkabk.azbarkon.app.core.designsystem.secondaryContainer
import abkabk.azbarkon.app.core.designsystem.secondaryFixed
import abkabk.azbarkon.app.core.designsystem.secondaryFixedDim
import abkabk.azbarkon.app.core.designsystem.surface
import abkabk.azbarkon.app.core.designsystem.surfaceBright
import abkabk.azbarkon.app.core.designsystem.surfaceContainer
import abkabk.azbarkon.app.core.designsystem.surfaceContainerHigh
import abkabk.azbarkon.app.core.designsystem.surfaceContainerHighest
import abkabk.azbarkon.app.core.designsystem.surfaceContainerLow
import abkabk.azbarkon.app.core.designsystem.surfaceContainerLowest
import abkabk.azbarkon.app.core.designsystem.surfaceDim
import abkabk.azbarkon.app.core.designsystem.surfaceTint
import abkabk.azbarkon.app.core.designsystem.surfaceVariant
import abkabk.azbarkon.app.core.designsystem.tertiary
import abkabk.azbarkon.app.core.designsystem.tertiaryContainer
import abkabk.azbarkon.app.core.designsystem.tertiaryFixed
import abkabk.azbarkon.app.core.designsystem.tertiaryFixedDim
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val LightColorScheme = lightColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    inversePrimary = inversePrimary,

    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,

    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,

    background = background,
    onBackground = onBackground,

    surface = surface,
    onSurface = onSurface,

    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,

    surfaceTint = surfaceTint,

    inverseSurface = inverseSurface,
    inverseOnSurface = inverseOnSurface,

    error = error,
    onError = onError,
    errorContainer = errorContainer,
    onErrorContainer = onErrorContainer,

    outline = outline,
    outlineVariant = outlineVariant,

    scrim = scrim,

    surfaceBright = surfaceBright,

    surfaceContainer = surfaceContainer,
    surfaceContainerHigh = surfaceContainerHigh,
    surfaceContainerHighest = surfaceContainerHighest,

    surfaceContainerLow = surfaceContainerLow,
    surfaceContainerLowest = surfaceContainerLowest,

    surfaceDim = surfaceDim,

    primaryFixed = primaryFixed,
    primaryFixedDim = primaryFixedDim,
    onPrimaryFixed = onPrimaryFixed,
    onPrimaryFixedVariant = onPrimaryFixedVariant,

    secondaryFixed = secondaryFixed,
    secondaryFixedDim = secondaryFixedDim,
    onSecondaryFixed = onSecondaryFixed,
    onSecondaryFixedVariant = onSecondaryFixedVariant,

    tertiaryFixed = tertiaryFixed,
    tertiaryFixedDim = tertiaryFixedDim,
    onTertiaryFixed = onTertiaryFixed,
    onTertiaryFixedVariant = onTertiaryFixedVariant
)

val DarkColorScheme = darkColorScheme(
    primary = darkPrimary,
    onPrimary = darkOnPrimary,
    primaryContainer = darkPrimaryContainer,
    onPrimaryContainer = darkOnPrimaryContainer,
    inversePrimary = darkInversePrimary,

    secondary = darkSecondary,
    onSecondary = darkOnSecondary,
    secondaryContainer = darkSecondaryContainer,
    onSecondaryContainer = darkOnSecondaryContainer,

    tertiary = darkTertiary,
    onTertiary = darkOnTertiary,
    tertiaryContainer = darkTertiaryContainer,
    onTertiaryContainer = darkOnTertiaryContainer,

    background = darkBackground,
    onBackground = darkOnBackground,

    surface = darkSurface,
    onSurface = darkOnSurface,

    surfaceVariant = darkSurfaceVariant,
    onSurfaceVariant = darkOnSurfaceVariant,

    surfaceTint = darkPrimary,

    inverseSurface = darkInverseSurface,
    inverseOnSurface = darkInverseOnSurface,

    error = darkError,
    onError = darkOnError,
    errorContainer = darkErrorContainer,
    onErrorContainer = onErrorContainer,

    outline = darkOutline,
    outlineVariant = darkOutlineVariant,

    scrim = scrim,

    surfaceBright = darkSurfaceBright,

    surfaceContainer = darkSurfaceContainer,
    surfaceContainerHigh = darkSurfaceContainerHigh,
    surfaceContainerHighest = darkSurfaceContainerHighest,

    surfaceContainerLow = darkSurfaceContainerLow,
    surfaceContainerLowest = darkSurfaceContainerLowest,

    surfaceDim = darkSurfaceDim,

    primaryFixed = primaryFixed,
    primaryFixedDim = primaryFixedDim,
    onPrimaryFixed = onPrimaryFixed,
    onPrimaryFixedVariant = onPrimaryFixedVariant,

    secondaryFixed = secondaryFixed,
    secondaryFixedDim = secondaryFixedDim,
    onSecondaryFixed = onSecondaryFixed,
    onSecondaryFixedVariant = onSecondaryFixedVariant,

    tertiaryFixed = tertiaryFixed,
    tertiaryFixedDim = tertiaryFixedDim,
    onTertiaryFixed = onTertiaryFixed,
    onTertiaryFixedVariant = onTertiaryFixedVariant
)