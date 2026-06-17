package abkabk.azbarkon.features.search.navigation

import abkabk.azbarkon.features.poets.navigation.PoemDetailRoute
import abkabk.azbarkon.features.search.SearchNavigationArgs
import abkabk.azbarkon.features.search.SearchRoot
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

fun NavController.navigateToSearch(
    poetId: Int? = null,
    catId: Int? = null,
) {
    navigate(SearchRoute(poetId = poetId, catId = catId))
}

fun NavGraphBuilder.searchGraph(
    navController: NavController,
) {
    composable<SearchRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<SearchRoute>()
        SearchRoot(
            initialPoetId = route.poetId,
            initialCatId = route.catId,
            onBackClick = navController::navigateUp,
            onNavigateToPoemDetail = { poemId ->
                navController.navigate(PoemDetailRoute(poemId = poemId))
            },
            viewModelArgs = SearchNavigationArgs(poetId = route.poetId, catId = route.catId),
        )
    }
}
