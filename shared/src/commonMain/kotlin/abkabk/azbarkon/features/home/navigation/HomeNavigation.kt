package abkabk.azbarkon.features.home.navigation

import abkabk.azbarkon.features.home.HomeRoot
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.homeGraph() {
    composable<HomeRoute> {
        HomeRoot()
    }
}
