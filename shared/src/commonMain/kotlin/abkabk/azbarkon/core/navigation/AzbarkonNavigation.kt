package abkabk.azbarkon.core.navigation

import abkabk.azbarkon.core.ui_base.LocalAzbarkonAppState
import abkabk.azbarkon.core.ui_base.rememberAzbarkonAppState
import abkabk.azbarkon.features.games.navigation.GamesRoute
import abkabk.azbarkon.features.games.navigation.gamesGraph
import abkabk.azbarkon.features.home.navigation.HomeRoute
import abkabk.azbarkon.features.home.navigation.MyPoemsRoute
import abkabk.azbarkon.features.home.navigation.homeGraph
import abkabk.azbarkon.features.poets.navigation.PoemDetailRoute
import abkabk.azbarkon.features.poets.navigation.PoetDetailRoute
import abkabk.azbarkon.features.poets.navigation.PoetsListRoute
import abkabk.azbarkon.features.poets.navigation.poetsGraph
import abkabk.azbarkon.features.profile.navigation.ProfileRoute
import abkabk.azbarkon.features.profile.navigation.profileGraph
import abkabk.azbarkon.features.tasvir_negar.navigation.TasvirNegarRoute
import abkabk.azbarkon.features.tasvir_negar.navigation.tasvirNegarGraph
import abkabk.azbarkon.features.memorization.navigation.memorizationGraph
import abkabk.azbarkon.features.memorization.navigation.navigateToActiveMemorization
import abkabk.azbarkon.features.memorization.navigation.navigateToMemorizationPractice
import abkabk.azbarkon.features.memorization.navigation.navigateToMemorizationSelect
import abkabk.azbarkon.features.search.navigation.navigateToSearch
import abkabk.azbarkon.features.search.navigation.searchGraph
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.arrow_back
import azbarkoncmp.shared.generated.resources.cd_back
import azbarkoncmp.shared.generated.resources.cd_search
import azbarkoncmp.shared.generated.resources.search
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private fun NavDestination?.isRootTabDestination(): Boolean =
    this != null &&
        (
            hasRoute<HomeRoute>() ||
                hasRoute<PoetsListRoute>() ||
                hasRoute<GamesRoute>() ||
                hasRoute<ProfileRoute>()
        )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzbarkonNavigation(
    initialPoemId: Int? = null,
    openMemorizationPractice: Boolean = false,
) {
    val navController = rememberNavController()
    val appState = rememberAzbarkonAppState()

    LaunchedEffect(initialPoemId) {
        initialPoemId?.let { poemId ->
            navController.navigate(PoemDetailRoute(poemId = poemId)) {
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(openMemorizationPractice) {
        if (openMemorizationPractice) {
            navController.navigateToMemorizationPractice(poemId = null)
        }
    }

    val items =
        listOf(
            BottomNavItem.Home,
            BottomNavItem.Treasure,
            BottomNavItem.Games,
            BottomNavItem.Profile,
        )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isRootDestination = currentDestination.isRootTabDestination()

    val currentItem =
        items.find { item ->
            when (item) {
                BottomNavItem.Home -> currentDestination?.hasRoute<HomeRoute>() == true
                BottomNavItem.Treasure -> currentDestination?.hasRoute<PoetsListRoute>() == true
                BottomNavItem.Games -> currentDestination?.hasRoute<GamesRoute>() == true
                BottomNavItem.Profile -> currentDestination?.hasRoute<ProfileRoute>() == true
            }
        }

    CompositionLocalProvider(LocalAzbarkonAppState provides appState) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(appState.snackbarHostState)
            },
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                if (isRootDestination) {
                    Box(
                        modifier =
                            Modifier
                                .windowInsetsPadding(WindowInsets.statusBars)
                                .fillMaxWidth()
                                .height(56.dp),
                    ) {
                        if (navController.previousBackStackEntry != null) {
                            IconButton(
                                modifier = Modifier.align(Alignment.CenterStart),
                                onClick = { navController.navigateUp() },
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.arrow_back),
                                    contentDescription = stringResource(Res.string.cd_back),
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = currentItem?.headerTitle?.let { stringResource(it) } ?: "",
                                style = MaterialTheme.typography.headlineLarge,
                            )

                            currentItem?.subtitle?.let {
                                Text(
                                    text = stringResource(it),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        if (currentDestination?.hasRoute<PoetsListRoute>() == true) {
                            IconButton(
                                modifier = Modifier.align(Alignment.CenterEnd),
                                onClick = {
                                    navController.navigateToSearch()
                                },
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.search),
                                    contentDescription = stringResource(Res.string.cd_search),
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                if (isRootDestination) {
                    NavigationBar(
                        modifier =
                            Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .fillMaxWidth()
                                .height(64.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                    ) {
                        items.forEach { item ->
                            val selected =
                                when (item) {
                                    BottomNavItem.Home -> currentDestination?.hasRoute<HomeRoute>() == true
                                    BottomNavItem.Treasure -> currentDestination?.hasRoute<PoetsListRoute>() == true
                                    BottomNavItem.Games -> currentDestination?.hasRoute<GamesRoute>() == true
                                    BottomNavItem.Profile -> currentDestination?.hasRoute<ProfileRoute>() == true
                                }

                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    val route =
                                        when (item) {
                                            BottomNavItem.Home -> HomeRoute
                                            BottomNavItem.Treasure -> PoetsListRoute
                                            BottomNavItem.Games -> GamesRoute
                                            BottomNavItem.Profile -> ProfileRoute
                                        }
                                    navController.navigate(route) {
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
                                        contentDescription = stringResource(item.title),
                                    )
                                },
                                colors =
                                    NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = Color.Transparent,
                                    ),
                                label = {
                                    Text(
                                        text = stringResource(item.title),
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                },
                                alwaysShowLabel = false,
                            )
                        }
                    }
                }
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
                modifier = Modifier.padding(padding),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None },
            ) {
                homeGraph(
                    onNavigateToPoetsList = {
                        navController.navigate(PoetsListRoute)
                    },
                    onNavigateToPoetDetail = { poetId ->
                        navController.navigate(PoetDetailRoute(poetId))
                    },
                    onNavigateToMyPoems = {
                        navController.navigate(MyPoemsRoute)
                    },
                    onNavigateToSearch = {
                        navController.navigateToSearch()
                    },
                    onBackFromMyPoems = navController::navigateUp,
                    onNavigateToPoemDetailFromMyPoems = { poemId ->
                        navController.navigate(PoemDetailRoute(poemId = poemId))
                    },
                    onNavigateToTasvirNegar = {
                        navController.navigate(TasvirNegarRoute(poemId = null))
                    },
                    onNavigateToMemorizationSelect = {
                        navController.navigateToMemorizationSelect()
                    },
                    onNavigateToMemorizationPractice = {
                        navController.navigateToMemorizationPractice()
                    },
                    onNavigateToActiveMemorization = {
                        navController.navigateToActiveMemorization()
                    },
                )
                tasvirNegarGraph(onBackClick = navController::navigateUp)
                memorizationGraph(navController)
                gamesGraph(navController)
                profileGraph()
                poetsGraph(navController)
                searchGraph(navController)
            }
        }
    }
}
