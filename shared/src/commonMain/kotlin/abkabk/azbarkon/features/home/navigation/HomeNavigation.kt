package abkabk.azbarkon.features.home.navigation

import abkabk.azbarkon.features.home.HomeRoot
import abkabk.azbarkon.features.my_poems.MyPoemsRoot
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.homeGraph(
    onNavigateToPoetsList: () -> Unit,
    onNavigateToPoetDetail: (Int) -> Unit,
    onNavigateToMyPoems: () -> Unit,
    onBackFromMyPoems: () -> Unit,
    onNavigateToPoemDetailFromMyPoems: (Int) -> Unit,
) {
    composable<HomeRoute> {
        HomeRoot(
            onNavigateToPoetsList = onNavigateToPoetsList,
            onNavigateToPoetDetail = onNavigateToPoetDetail,
            onNavigateToMyPoems = onNavigateToMyPoems,
        )
    }

    composable<MyPoemsRoute> {
        MyPoemsRoot(
            onBackClick = onBackFromMyPoems,
            onNavigateToPoemDetail = onNavigateToPoemDetailFromMyPoems,
        )
    }
}
