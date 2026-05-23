package abkabk.azbarkon.app.core.navigation

import abkabk.azbarkon.app.core.presentation.rememberAzbarkonAppState
import abkabk.azbarkon.app.features.games.GamesScreen
import abkabk.azbarkon.app.features.home.HomeScreen
import abkabk.azbarkon.app.features.library.LibraryScreen
import abkabk.azbarkon.app.features.profile.ProfileScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import azbarkoncmp.composeapp.generated.resources.Res
import azbarkoncmp.composeapp.generated.resources.arrow_back
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
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

    val currentItem = items.find { it.route == currentRoute }

    Scaffold(
        snackbarHost = {
            SnackbarHost(appState.snackbarHostState)
        },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Box(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .fillMaxWidth()
                    .height(56.dp)
            ) {

                if (navController.previousBackStackEntry != null) {
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_back),
                            contentDescription = "Back"
                        )
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentItem?.headerTitle?.let { stringResource(it) } ?: "",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    currentItem?.subtitle?.let{
                        Text(
                            text = stringResource(it),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                }
            }
        },
        bottomBar = {

            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .fillMaxWidth()
                    .height(64.dp),
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
                                modifier = Modifier.size(22.dp),
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
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        alwaysShowLabel = false
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