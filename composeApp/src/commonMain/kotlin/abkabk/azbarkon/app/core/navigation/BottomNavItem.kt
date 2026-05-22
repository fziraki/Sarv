package abkabk.azbarkon.app.core.navigation

import azbarkoncmp.composeapp.generated.resources.Res
import azbarkoncmp.composeapp.generated.resources.app_name
import azbarkoncmp.composeapp.generated.resources.games
import azbarkoncmp.composeapp.generated.resources.games_subtitle
import azbarkoncmp.composeapp.generated.resources.home
import azbarkoncmp.composeapp.generated.resources.library
import azbarkoncmp.composeapp.generated.resources.profile
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class BottomNavItem(
    val route: String,
    val title: StringResource,
    val icon: DrawableResource,
    val headerTitle: StringResource,
    val subtitle: StringResource? = null
) {

    data object Home : BottomNavItem(
        route = Routes.HOME,
        title = Res.string.home,
        icon = Res.drawable.home,
        headerTitle = Res.string.app_name
    )

    data object Library : BottomNavItem(
        route = Routes.LIBRARY,
        title = Res.string.library,
        icon = Res.drawable.library,
        headerTitle = Res.string.library
    )

    data object Games : BottomNavItem(
        route = Routes.GAMES,
        title = Res.string.games,
        icon = Res.drawable.games,
        headerTitle = Res.string.games,
        subtitle = Res.string.games_subtitle
    )

    data object Profile : BottomNavItem(
        route = Routes.PROFILE,
        title = Res.string.profile,
        icon = Res.drawable.profile,
        headerTitle = Res.string.profile
    )
}