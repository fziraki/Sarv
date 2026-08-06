package abkabk.azbarkon.features.home.navigation

import abkabk.azbarkon.features.home.HomeCallbacks
import abkabk.azbarkon.features.home.HomeRoot
import abkabk.azbarkon.features.mypoems.MyPoemsRoot
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.homeGraph(
    callbacks: HomeCallbacks,
    onBackFromMyPoems: () -> Unit,
    onNavigateToPoemDetailFromMyPoems: (Int) -> Unit,
) {
    composable<HomeRoute> {
        HomeRoot(callbacks = callbacks)
    }

    composable<MyPoemsRoute> {
        MyPoemsRoot(
            onBackClick = onBackFromMyPoems,
            onNavigateToPoemDetail = onNavigateToPoemDetailFromMyPoems,
        )
    }
}
