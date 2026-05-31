package abkabk.azbarkon.features.profile.navigation

import abkabk.azbarkon.features.profile.ProfileRoot
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.profileGraph() {
    composable<ProfileRoute> {
        ProfileRoot()
    }
}
