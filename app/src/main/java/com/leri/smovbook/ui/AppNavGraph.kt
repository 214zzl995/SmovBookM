package com.leri.smovbook.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.leri.smovbook.ui.barcodeScann.BarCodeScannRoute
import com.leri.smovbook.ui.home.HomeRoute
import com.leri.smovbook.ui.home.HomeViewModel
import com.leri.smovbook.ui.setting.SettingsRoute
import com.leri.smovbook.ui.smovDetail.SmovDetailRouter
import com.leri.smovbook.ui.splash.SplashScreen

//开屏动画  https://github.com/stevdza-san/AnimatedSplashScreenDemo
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestinations.HOME_SCREEN.getRouteWithArguments(),
) {


    val homeViewModel: HomeViewModel = hiltViewModel()
    val navigationActions = remember(navController) {
        AppNavigationActions(navController)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry?.destination?.route ?: AppDestinations.HOME_SCREEN.getRouteWithArguments()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier.height(0.dp),
            )
        },
        bottomBar = {
            val items = listOf(
                AppDestinations.HOME_SCREEN,
                AppDestinations.SETTINGS_SCREEN,
            )

            NavigationBar(
                modifier = Modifier
                    .animateContentSize()
            ) {
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    screen.icon!!
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.destination) },
                        label = { Text(screen.destination) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.getRouteWithArguments())
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        //如何处理动画在 https://google.github.io/accompanist/navigation-animation/
        NavHost(
            navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                AppDestinations.SPLASH_SCREEN.getRouteWithArguments(),
                enterTransition = {
                    when (initialState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                },
                popEnterTransition = {
                    when (initialState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                },
                popExitTransition = {
                    when (targetState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                },
            ) {
                SplashScreen {
                    navigationActions.navigateToHome()
                }
            }
            composable(
                AppDestinations.HOME_SCREEN.getRouteWithArguments(),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                },
            ) {
                HomeRoute(
                    homeViewModel = homeViewModel,
                    navigationActions = navigationActions,
                    currentRoute = currentRoute,
                    openSmovDetail = navigationActions.navigateToSmovDetail
                )
            }
            composable(AppDestinations.BARCODE_SCREEN.getRouteWithArguments(),
                enterTransition = {
                    when (initialState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                },
                popEnterTransition = {
                    when (initialState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                },
                popExitTransition = {
                    when (targetState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                }) {
                BarCodeScannRoute(
                    navigateUp = { navController.navigateUp() },
                    changeServer = {
                        homeViewModel.changeServerUrl(it)
                    }
                )
            }
            composable(AppDestinations.SETTINGS_SCREEN.getRouteWithArguments(),
                enterTransition = {
                    when (initialState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                },
                popEnterTransition = {
                    when (initialState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                },
                popExitTransition = {
                    when (targetState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                }) {
                SettingsRoute(
                    navigateUp = { navController.navigateUp() },
                )
            }
            composable(
                route = AppDestinations.SMOV_DETAIL_SCREEN.getRouteWithArguments(),
                arguments =
                AppDestinations.SMOV_DETAIL_SCREEN.arguments.map {
                    navArgument(it.first, builder = it.second)
                } + AppDestinations.SMOV_DETAIL_SCREEN.path.map {
                    navArgument(it.first, builder = it.second)
                },
                enterTransition = {
                    when (initialState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                },
                popEnterTransition = {
                    when (initialState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                },
                popExitTransition = {
                    when (targetState.destination.route) {
                        AppDestinations.HOME_SCREEN.getRouteWithArguments() ->
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )

                        else -> null
                    }
                }) { backStackEntry ->

                val smovId =
                    backStackEntry.arguments?.getLong("smov_id")
                        ?: return@composable
                val smovName =
                    backStackEntry.arguments?.getString("smov_name")
                        ?: return@composable

                val serverState by homeViewModel.serverState.collectAsState()

                SmovDetailRouter(
                    smovId,
                    smovName,
                    serverUrl = serverState.serverUrl,
                    hiltViewModel()
                ) { navController.navigateUp() }

            }
        }
    }



    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        var selectedItem by remember { mutableIntStateOf(0) }
        val items = listOf("Songs", "Artists", "Playlists")

        NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                    label = { Text(item) },
                    selected = selectedItem == index,
                    onClick = { selectedItem = index }
                )
            }
        }
    }


}