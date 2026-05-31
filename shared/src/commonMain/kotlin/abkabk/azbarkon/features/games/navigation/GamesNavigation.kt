package abkabk.azbarkon.features.games.navigation

import abkabk.azbarkon.features.games.GamesRoot
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.gamesGraph() {
    composable<GamesRoute> {
        GamesRoot()
    }
}
