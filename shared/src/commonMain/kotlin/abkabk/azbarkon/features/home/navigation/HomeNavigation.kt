package abkabk.azbarkon.features.home.navigation

import abkabk.azbarkon.features.home.HomeRoot
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.homeGraph(
    onNavigateToPoetsList: () -> Unit,
    onNavigateToPoetDetail: (Int) -> Unit,
) {
    composable<HomeRoute> {
        HomeRoot(
            onNavigateToPoetsList = onNavigateToPoetsList,
            onNavigateToPoetDetail = onNavigateToPoetDetail,
        )
    }
}
