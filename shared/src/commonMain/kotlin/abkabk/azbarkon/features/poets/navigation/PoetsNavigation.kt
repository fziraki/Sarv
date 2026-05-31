package abkabk.azbarkon.features.poets.navigation

import abkabk.azbarkon.features.poets.PoetDetailRoot
import abkabk.azbarkon.features.poets.PoetsListRoot
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

fun NavGraphBuilder.poetsGraph(
    navController: NavController,
) {
    composable<PoetsListRoute> {
        PoetsListRoot(
            onNavigateToPoetDetail = { poetId ->
                navController.navigate(PoetDetailRoute(poetId))
            },
            onBackClick = navController::navigateUp,
        )
    }

    composable<PoetDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<PoetDetailRoute>()
        PoetDetailRoot(
            poetId = route.poetId,
            onBackClick = navController::navigateUp,
        )
    }
}
