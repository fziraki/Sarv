package abkabk.azbarkon.ui.theme

import abkabk.azbarkon.core.designsystem.background
import abkabk.azbarkon.core.designsystem.darkBackground
import abkabk.azbarkon.core.designsystem.darkError
import abkabk.azbarkon.core.designsystem.darkErrorContainer
import abkabk.azbarkon.core.designsystem.darkInverseOnSurface
import abkabk.azbarkon.core.designsystem.darkInversePrimary
import abkabk.azbarkon.core.designsystem.darkInverseSurface
import abkabk.azbarkon.core.designsystem.darkOnBackground
import abkabk.azbarkon.core.designsystem.darkOnError
import abkabk.azbarkon.core.designsystem.darkOnPrimary
import abkabk.azbarkon.core.designsystem.darkOnPrimaryContainer
import abkabk.azbarkon.core.designsystem.darkOnSecondary
import abkabk.azbarkon.core.designsystem.darkOnSecondaryContainer
import abkabk.azbarkon.core.designsystem.darkOnSurface
import abkabk.azbarkon.core.designsystem.darkOnSurfaceVariant
import abkabk.azbarkon.core.designsystem.darkOnTertiary
import abkabk.azbarkon.core.designsystem.darkOnTertiaryContainer
import abkabk.azbarkon.core.designsystem.darkOutline
import abkabk.azbarkon.core.designsystem.darkOutlineVariant
import abkabk.azbarkon.core.designsystem.darkPrimary
import abkabk.azbarkon.core.designsystem.darkPrimaryContainer
import abkabk.azbarkon.core.designsystem.darkSecondary
import abkabk.azbarkon.core.designsystem.darkSecondaryContainer
import abkabk.azbarkon.core.designsystem.darkSurface
import abkabk.azbarkon.core.designsystem.darkSurfaceBright
import abkabk.azbarkon.core.designsystem.darkSurfaceContainer
import abkabk.azbarkon.core.designsystem.darkSurfaceContainerHigh
import abkabk.azbarkon.core.designsystem.darkSurfaceContainerHighest
import abkabk.azbarkon.core.designsystem.darkSurfaceContainerLow
import abkabk.azbarkon.core.designsystem.darkSurfaceContainerLowest
import abkabk.azbarkon.core.designsystem.darkSurfaceDim
import abkabk.azbarkon.core.designsystem.darkSurfaceVariant
import abkabk.azbarkon.core.designsystem.darkTertiary
import abkabk.azbarkon.core.designsystem.darkTertiaryContainer
import abkabk.azbarkon.core.designsystem.error
import abkabk.azbarkon.core.designsystem.errorContainer
import abkabk.azbarkon.core.designsystem.inverseOnSurface
import abkabk.azbarkon.core.designsystem.inversePrimary
import abkabk.azbarkon.core.designsystem.inverseSurface
import abkabk.azbarkon.core.designsystem.onBackground
import abkabk.azbarkon.core.designsystem.onError
import abkabk.azbarkon.core.designsystem.onErrorContainer
import abkabk.azbarkon.core.designsystem.onPrimary
import abkabk.azbarkon.core.designsystem.onPrimaryContainer
import abkabk.azbarkon.core.designsystem.onPrimaryFixed
import abkabk.azbarkon.core.designsystem.onPrimaryFixedVariant
import abkabk.azbarkon.core.designsystem.onSecondary
import abkabk.azbarkon.core.designsystem.onSecondaryContainer
import abkabk.azbarkon.core.designsystem.onSecondaryFixed
import abkabk.azbarkon.core.designsystem.onSecondaryFixedVariant
import abkabk.azbarkon.core.designsystem.onSurface
import abkabk.azbarkon.core.designsystem.onSurfaceVariant
import abkabk.azbarkon.core.designsystem.onTertiary
import abkabk.azbarkon.core.designsystem.onTertiaryContainer
import abkabk.azbarkon.core.designsystem.onTertiaryFixed
import abkabk.azbarkon.core.designsystem.onTertiaryFixedVariant
import abkabk.azbarkon.core.designsystem.outline
import abkabk.azbarkon.core.designsystem.outlineVariant
import abkabk.azbarkon.core.designsystem.primary
import abkabk.azbarkon.core.designsystem.primaryContainer
import abkabk.azbarkon.core.designsystem.primaryFixed
import abkabk.azbarkon.core.designsystem.primaryFixedDim
import abkabk.azbarkon.core.designsystem.scrim
import abkabk.azbarkon.core.designsystem.secondary
import abkabk.azbarkon.core.designsystem.secondaryContainer
import abkabk.azbarkon.core.designsystem.secondaryFixed
import abkabk.azbarkon.core.designsystem.secondaryFixedDim
import abkabk.azbarkon.core.designsystem.surface
import abkabk.azbarkon.core.designsystem.surfaceBright
import abkabk.azbarkon.core.designsystem.surfaceContainer
import abkabk.azbarkon.core.designsystem.surfaceContainerHigh
import abkabk.azbarkon.core.designsystem.surfaceContainerHighest
import abkabk.azbarkon.core.designsystem.surfaceContainerLow
import abkabk.azbarkon.core.designsystem.surfaceContainerLowest
import abkabk.azbarkon.core.designsystem.surfaceDim
import abkabk.azbarkon.core.designsystem.surfaceTint
import abkabk.azbarkon.core.designsystem.surfaceVariant
import abkabk.azbarkon.core.designsystem.tertiary
import abkabk.azbarkon.core.designsystem.tertiaryContainer
import abkabk.azbarkon.core.designsystem.tertiaryFixed
import abkabk.azbarkon.core.designsystem.tertiaryFixedDim
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