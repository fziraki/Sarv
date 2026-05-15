package abkabk.azbarkon.app.core.navigation

import azbarkoncmp.composeapp.generated.resources.Res
import azbarkoncmp.composeapp.generated.resources.home
import org.jetbrains.compose.resources.DrawableResource

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: DrawableResource
) {

    data object Home : BottomNavItem(
        route = "home",
        title = "آستان",
        icon = Res.drawable.home
    )

    data object Library : BottomNavItem(
        route = "library",
        title = "گنجینه",
        icon = Res.drawable.home
    )

    data object Games : BottomNavItem(
        route = "games",
        title = "بازی",
        icon = Res.drawable.home
    )

    data object Profile : BottomNavItem(
        route = "profile",
        title = "من",
        icon = Res.drawable.home
    )
}