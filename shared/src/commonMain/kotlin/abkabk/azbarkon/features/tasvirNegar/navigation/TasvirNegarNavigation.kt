package abkabk.azbarkon.features.tasvirNegar.navigation

import abkabk.azbarkon.features.tasvirNegar.TasvirNegarRoot
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
            initialText = route.initialText,
            onBackClick = onBackClick,
        )
    }
}
