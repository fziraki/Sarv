package abkabk.azbarkon.features.poets.navigation

import abkabk.azbarkon.features.chat.ChatRoot
import abkabk.azbarkon.features.poems.details.PoemDetailRoot
import abkabk.azbarkon.features.poems.list.PoemListRoot
import abkabk.azbarkon.features.poets.details.PoetDetailRoot
import abkabk.azbarkon.features.poets.list.PoetsListRoot
import abkabk.azbarkon.features.memorization.navigation.navigateToMemorizationPractice
import abkabk.azbarkon.features.search.navigation.navigateToSearch
import abkabk.azbarkon.features.tasvir_negar.navigation.TasvirNegarRoute
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
            onNavigateToChat = { poetId ->
                navController.navigate(ChatRoute(poetId))
            },
        )
    }

    composable<PoetDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<PoetDetailRoute>()
        PoetDetailRoot(
            poetId = route.poetId,
            onBackClick = navController::navigateUp,
            onNavigateToPoemList = { catId, title ->
                navController.navigate(PoemListRoute(catId = catId, title = title))
            },
            onNavigateToSearch = {
                navController.navigateToSearch(poetId = route.poetId)
            },
            onNavigateToChat = {
                navController.navigate(ChatRoute(route.poetId))
            },
        )
    }

    composable<PoemListRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<PoemListRoute>()
        PoemListRoot(
            catId = route.catId,
            title = route.title,
            onBackClick = navController::navigateUp,
            onNavigateToPoemDetail = { poemId ->
                navController.navigate(PoemDetailRoute(poemId = poemId))
            },
            onNavigateToSearch = {
                navController.navigateToSearch(catId = route.catId)
            }
        )
    }

    composable<PoemDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<PoemDetailRoute>()
        PoemDetailRoot(
            poemId = route.poemId,
            onBackClick = navController::navigateUp,
            onNavigateToTasvirNegar = { poemId ->
                navController.navigate(TasvirNegarRoute(poemId = poemId))
            },
            onNavigateToMemorizationPractice = { poemId ->
                navController.navigateToMemorizationPractice(poemId = poemId)
            },
        )
    }

    composable<ChatRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ChatRoute>()
        ChatRoot(
            poetId = route.poetId,
            onBackClick = navController::navigateUp,
        )
    }
}
