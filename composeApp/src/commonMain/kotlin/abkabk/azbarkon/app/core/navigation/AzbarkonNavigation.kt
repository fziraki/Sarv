package abkabk.azbarkon.app.core.navigation

import abkabk.azbarkon.app.core.presentation.rememberAzbarkonAppState
import abkabk.azbarkon.app.features.games.GamesScreen
import abkabk.azbarkon.app.features.home.HomeScreen
import abkabk.azbarkon.app.features.library.LibraryScreen
import abkabk.azbarkon.app.features.profile.ProfileScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AzbarkonNavigation() {

    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Library,
        BottomNavItem.Games,
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val appState = rememberAzbarkonAppState()

    Scaffold(
        snackbarHost = {
            SnackbarHost(appState.snackbarHostState)
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {

            NavigationBar(
                modifier = Modifier,
                containerColor = MaterialTheme.colorScheme.surface,
            ) {

                items.forEach { item ->

                    NavigationBarItem(

                        selected = currentRoute == item.route,

                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },

                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = stringResource(item.title)
                            )
                        },

                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = Color.Transparent
                        ),

                        label = {
                            Text(
                                text = stringResource(item.title),
                                style = if (currentRoute == item.route)
                                    MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                else
                                    MaterialTheme.typography.labelMedium
                            )
                        },
                    )
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            Napier.d("NavHost loaded")

            composable(BottomNavItem.Home.route) {
                HomeScreen()
            }

            composable(BottomNavItem.Library.route) {
                LibraryScreen()
            }

            composable(BottomNavItem.Games.route) {
                GamesScreen()
            }

            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }

        }
    }
}