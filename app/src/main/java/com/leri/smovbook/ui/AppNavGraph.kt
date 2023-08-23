package com.leri.smovbook.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.leri.smovbook.ui.barcodeScann.BarCodeScannRoute
import com.leri.smovbook.ui.home.HomeRoute
import com.leri.smovbook.ui.home.HomeViewModel
import com.leri.smovbook.ui.serviceSwitch.ServiceSwitchRoute
import com.leri.smovbook.ui.setting.SettingsRoute
import com.leri.smovbook.ui.smovDetail.SmovDetailRouter
import com.leri.smovbook.ui.splash.SplashScreen
import kotlinx.coroutines.launch

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

    NavHost(
        navController,
        startDestination = startDestination,
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

            NavigationHomePage(
                homeViewModel = homeViewModel,
                navigationActions = navigationActions,
                currentRoute = currentRoute,
                navController = navController,
                modifier = modifier,
                navBackStackEntry = navBackStackEntry
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationHomePage(
    homeViewModel: HomeViewModel,
    navigationActions: AppNavigationActions,
    currentRoute: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    navBackStackEntry: NavBackStackEntry? = null,
) {

    val coroutineScope = rememberCoroutineScope()
    val serviceSwitch = @Composable {
        ServiceSwitchRoute(
            homeViewModel = homeViewModel,
            navigationActions = navigationActions,
        )
    }
    val home = @Composable {
        HomeRoute(
            homeViewModel = homeViewModel,
            navigationActions = navigationActions,
            currentRoute = currentRoute,
            openSmovDetail = navigationActions.navigateToSmovDetail
        )
    }
    val settings = @Composable {
        SettingsRoute(
            navigateUp = { navController.navigateUp() },
        )
    }

    //是侦测不到 State 内部的变动的 所以不会重组 问题来了 是否应该把这个部分 单独做viewmodel
    val serverState by homeViewModel.serverUrl

    val screens = listOf(
        AppDestinations.HOME_SCREEN to home,
        AppDestinations.SERVICE_SWITCH_SCREEN to serviceSwitch,
        AppDestinations.SETTINGS_SCREEN to settings,
        )
    val pagerState = rememberPagerState(pageCount = {
        screens.size
    }, initialPage = 0)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier.height(20.dp),
            ){
                Text(text = serverState)
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .animateContentSize()
            ) {
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.first.icon!!,
                                contentDescription = screen.first.destination
                            )
                        },
                        label = { Text(screen.first.destination) },
                        selected = pagerState.currentPage == screens.indexOf(screen),
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(screens.indexOf(screen))
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding),
            userScrollEnabled = false
        ) { page ->
            val screen = screens[page]
            screen.second()
        }
    }
}