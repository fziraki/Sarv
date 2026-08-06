package abkabk.azbarkon.features.games.navigation

import abkabk.azbarkon.domain.model.games.GameSessionSummary
import abkabk.azbarkon.domain.model.games.GameType
import abkabk.azbarkon.features.games.GamesRoot
import abkabk.azbarkon.features.games.session.GameResultRoot
import abkabk.azbarkon.features.games.session.GameSessionRoot
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

fun NavGraphBuilder.gamesGraph(
    navController: NavController,
) {
    composable<GamesRoute> {
        GamesRoot(
            onNavigateToGame = { type ->
                navController.navigate(GamePlayRoute(type = type.toRoute()))
            },
        )
    }

    composable<GamePlayRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<GamePlayRoute>()
        GameSessionRoot(
            gameTypeRoute = route.type,
            onBackClick = navController::navigateUp,
            onNavigateToResult = { gameType, summary ->
                navController.navigate(summary.toResultRoute(gameType)) {
                    popUpTo<GamePlayRoute> { inclusive = true }
                }
            },
        )
    }

    composable<GameResultRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<GameResultRoute>()
        GameResultRoot(
            correctCount = route.correct,
            wrongCount = route.wrong,
            noAnswerCount = route.noAnswer,
            scoreDelta = route.scoreDelta,
            onReplayClick = {
                navController.navigate(GamePlayRoute(type = route.type)) {
                    popUpTo<GameResultRoute> { inclusive = true }
                }
            },
            onBackToListClick = {
                navController.navigate(GamesRoute) {
                    popUpTo<GamesRoute> { inclusive = false }
                    launchSingleTop = true
                }
            },
        )
    }
}

private fun GameSessionSummary.toResultRoute(gameType: GameType): GameResultRoute =
    GameResultRoute(
        type = gameType.toRoute(),
        correct = correctCount,
        wrong = wrongCount,
        noAnswer = noAnswerCount,
        scoreDelta = scoreDelta,
    )

fun NavController.navigateToGame(type: GameTypeRoute) {
    navigate(GamePlayRoute(type = type))
}
