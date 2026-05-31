package abkabk.azbarkon.features.library.navigation

import abkabk.azbarkon.features.library.LibraryRoot
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.libraryGraph() {
    composable<LibraryRoute> {
        LibraryRoot()
    }
}
