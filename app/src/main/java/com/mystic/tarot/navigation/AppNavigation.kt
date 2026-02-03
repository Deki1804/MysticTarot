package com.mystic.tarot.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mystic.tarot.feature.home.HomeScreen
import com.mystic.tarot.feature.journal.JournalScreen
import com.mystic.tarot.feature.reading.ReadingScreen
import com.mystic.tarot.feature.shop.ShopScreen
import com.mystic.tarot.feature.settings.SettingsScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Početna", Icons.Default.Home)
    object Reading : Screen("reading", "Čitanje", Icons.Default.Visibility)
    object Journal : Screen("journal", "Dnevnik", Icons.Default.Book)
    object Shop : Screen("shop", "Trgovina", Icons.Default.ShoppingCart)
    object Settings : Screen("settings", "Postavke", Icons.Default.Settings)
}

val items = listOf(
    Screen.Home,
    Screen.Reading,
    Screen.Journal,
    Screen.Shop
)

@Composable
fun AppNavigation(
    shopViewModelFactory: com.mystic.tarot.feature.shop.ShopViewModelFactory,
    adManager: com.mystic.tarot.core.ads.AdManager,
    journalRepository: com.mystic.tarot.core.data.JournalRepository,
    analyticsHelper: com.mystic.tarot.core.analytics.AnalyticsHelper,
    userId: String,
    coinRepository: com.mystic.tarot.core.data.CoinRepository,
    settingsRepository: com.mystic.tarot.core.data.SettingsRepository
) {
    val navController = rememberNavController()

    com.mystic.tarot.ui.components.MysticBackground {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(
                    route = Screen.Home.route,
                    enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn(animationSpec = tween(500)) },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut(animationSpec = tween(500)) }
                ) { HomeScreen(navController = navController, coinRepository = coinRepository) }
                
                composable(
                    route = Screen.Reading.route,
                    enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn(animationSpec = tween(500)) },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut(animationSpec = tween(500)) }
                ) { 
                    ReadingScreen(
                        journalRepository = journalRepository,
                        analyticsHelper = analyticsHelper,
                        userId = userId,
                        coinRepository = coinRepository,
                        adManager = adManager
                    ) 
                }
                
                composable(
                    route = Screen.Journal.route,
                    enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn(animationSpec = tween(500)) },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut(animationSpec = tween(500)) }
                ) { 
                    val journalFactory = com.mystic.tarot.feature.journal.JournalViewModelFactory(journalRepository, userId)
                    val journalViewModel: com.mystic.tarot.feature.journal.JournalViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = journalFactory)
                    JournalScreen(viewModel = journalViewModel)
                }
                
                composable(
                    route = Screen.Shop.route,
                    enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn(animationSpec = tween(500)) },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut(animationSpec = tween(500)) }
                ) { 
                    val shopViewModel: com.mystic.tarot.feature.shop.ShopViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = shopViewModelFactory)
                    ShopScreen(viewModel = shopViewModel, adManager = adManager) 
                }

                composable(route = "settings") {
                     SettingsScreen(
                        settingsRepository = settingsRepository,
                        onBackClick = { navController.popBackStack() }
                     )
                }
            }
        }
    }
}
