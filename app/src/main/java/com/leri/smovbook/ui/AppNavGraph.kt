package com.leri.smovbook.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.leri.smovbook.ui.barcodeScann.BarCodeScannRoute
import com.leri.smovbook.ui.home.HomeRoute
import com.leri.smovbook.ui.home.HomeViewModel
import com.leri.smovbook.ui.smovDetail.SmovDetailRouter
import com.leri.smovbook.ui.splash.SplashScreen

//开屏动画  https://github.com/stevdza-san/AnimatedSplashScreenDemo
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberAnimatedNavController(),
    startDestination: String = AppDestinations.HOME_ROUTE,
) {


    val homeViewModel: HomeViewModel = hiltViewModel()
    val navigationActions = remember(navController) {
        AppNavigationActions(navController)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route ?: AppDestinations.HOME_ROUTE

    //如何处理动画在 https://google.github.io/accompanist/navigation-animation/
    AnimatedNavHost(
        navController, startDestination = startDestination, modifier = modifier
    ) {
        composable(
            AppDestinations.SPLASH_SCREEN,
            enterTransition = {
                when (initialState.destination.route) {
                    AppDestinations.HOME_ROUTE ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(500)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    AppDestinations.HOME_ROUTE ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Start,
                            animationSpec = tween(500)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    AppDestinations.HOME_ROUTE ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(500)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    AppDestinations.HOME_ROUTE ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
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
            AppDestinations.HOME_ROUTE,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(500)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(500)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Right,
                    animationSpec = tween(500)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Right,
                    animationSpec = tween(500)
                )
            },
        ) {
            HomeRoute(
                homeViewModel = homeViewModel,
                openBarScann = navigationActions.navigateToBarCode,
                currentRoute = currentRoute,
                openSmovDetail = navigationActions.navigateToSmovDetail
            )
        }
        composable(AppDestinations.BARCODE_ROUTE,
            enterTransition = {
                when (initialState.destination.route) {
                    AppDestinations.HOME_ROUTE ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(500)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    AppDestinations.HOME_ROUTE ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(500)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    AppDestinations.HOME_ROUTE ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(500)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    AppDestinations.HOME_ROUTE ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
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
        composable(
            route = AppDestinations.SMOV_DETAIL_WITH_ARGUMENT,
            arguments = listOf(
                navArgument(AppDestinations.SMOV_DETAIL_ARGUMENT0) { type = NavType.LongType },
                navArgument(AppDestinations.SMOV_DETAIL_ARGUMENT1) { defaultValue = "SmovBook" }
            ),
            enterTransition = {
                when (initialState.destination.route) {
                    AppDestinations.HOME_ROUTE ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(500)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    AppDestinations.HOME_ROUTE ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(500)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    AppDestinations.HOME_ROUTE ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(500)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    AppDestinations.HOME_ROUTE ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(500)
                        )
                    else -> null
                }
            }) { backStackEntry ->

            val smovId = backStackEntry.arguments?.getLong(AppDestinations.SMOV_DETAIL_ARGUMENT0)
                ?: return@composable
            val smovName =
                backStackEntry.arguments?.getString(AppDestinations.SMOV_DETAIL_ARGUMENT1)
                    ?: return@composable

            val serverState by homeViewModel.serverState

            SmovDetailRouter(
                smovId,
                smovName,
                serverUrl = serverState.serverUrl,
                hiltViewModel()
            ) { navController.navigateUp() }

        }
    }
}