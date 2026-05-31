package abkabk.azbarkon.core.navigation

import abkabk.azbarkon.features.games.navigation.GamesRoute
import abkabk.azbarkon.features.home.navigation.HomeRoute
import abkabk.azbarkon.features.library.navigation.LibraryRoute
import abkabk.azbarkon.features.profile.navigation.ProfileRoute
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.app_name
import azbarkoncmp.shared.generated.resources.games
import azbarkoncmp.shared.generated.resources.games_subtitle
import azbarkoncmp.shared.generated.resources.home
import azbarkoncmp.shared.generated.resources.library
import azbarkoncmp.shared.generated.resources.profile
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import kotlin.reflect.KClass

sealed class BottomNavItem(
    val route: KClass<*>,
    val title: StringResource,
    val icon: DrawableResource,
    val headerTitle: StringResource,
    val subtitle: StringResource? = null,
) {
    data object Home : BottomNavItem(
        route = HomeRoute::class,
        title = Res.string.home,
        icon = Res.drawable.home,
        headerTitle = Res.string.app_name,
    )

    data object Library : BottomNavItem(
        route = LibraryRoute::class,
        title = Res.string.library,
        icon = Res.drawable.library,
        headerTitle = Res.string.library,
    )

    data object Games : BottomNavItem(
        route = GamesRoute::class,
        title = Res.string.games,
        icon = Res.drawable.games,
        headerTitle = Res.string.games,
        subtitle = Res.string.games_subtitle,
    )

    data object Profile : BottomNavItem(
        route = ProfileRoute::class,
        title = Res.string.profile,
        icon = Res.drawable.profile,
        headerTitle = Res.string.profile,
    )
}
