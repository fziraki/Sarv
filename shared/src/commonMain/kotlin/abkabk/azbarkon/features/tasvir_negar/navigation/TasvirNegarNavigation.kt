package abkabk.azbarkon.features.tasvir_negar.navigation

import abkabk.azbarkon.features.tasvir_negar.TasvirNegarRoot
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

fun NavGraphBuilder.tasvirNegarGraph(
    onBackClick: () -> Unit,
) {
    composable<TasvirNegarRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<TasvirNegarRoute>()
        TasvirNegarRoot(
            poemId = route.poemId,
            onBackClick = onBackClick,
        )
    }
}
