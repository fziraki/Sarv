package abkabk.azbarkon.app.core.navigation

import azbarkoncmp.composeapp.generated.resources.Res
import azbarkoncmp.composeapp.generated.resources.games
import azbarkoncmp.composeapp.generated.resources.home
import azbarkoncmp.composeapp.generated.resources.library
import azbarkoncmp.composeapp.generated.resources.profile
import org.jetbrains.compose.resources.DrawableResource

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: DrawableResource
) {

    data object Home : BottomNavItem(
        route = Routes.HOME,
        title = "آستان",
        icon = Res.drawable.home
    )

    data object Library : BottomNavItem(
        route = Routes.LIBRARY,
        title = "گنجینه",
        icon = Res.drawable.library
    )

    data object Games : BottomNavItem(
        route = Routes.GAMES,
        title = "میدان",
        icon = Res.drawable.games
    )

    data object Profile : BottomNavItem(
        route = Routes.PROFILE,
        title = "من",
        icon = Res.drawable.profile
    )
}