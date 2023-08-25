package com.leri.smovbook.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.leri.smovbook.ui.barcodeScann.BarCodeScannRoute
import com.leri.smovbook.ui.home.HomeRoute
import com.leri.smovbook.viewModel.HomeViewModel
import com.leri.smovbook.ui.serviceSwitch.ServiceSwitchRoute
import com.leri.smovbook.ui.setting.SettingsRoute
import com.leri.smovbook.ui.smovDetail.SmovDetailRouter
import com.leri.smovbook.ui.splash.SplashScreen
import com.leri.smovbook.viewModel.ServiceViewModel
import com.leri.smovbook.viewModel.SettingsViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestinations.INDEX_SCREEN.getRouteWithArguments(),
) {


    val homeViewModel: HomeViewModel = hiltViewModel()
    val serviceViewModel: ServiceViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val navigationActions = remember(navController) {
        AppNavigationActions(navController)
    }

    NavHost(
        navController,
        startDestination = startDestination,
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
        }
    ) {
        composable(
            AppDestinations.SPLASH_SCREEN.getRouteWithArguments(),
        ) {
            SplashScreen {
                navigationActions.navigateToHome()
            }
        }
        composable(
            AppDestinations.INDEX_SCREEN.getRouteWithArguments(),
        ) {
            NavHomePage(
                homeViewModel = homeViewModel,
                serviceViewModel = serviceViewModel,
                navigationActions = navigationActions,
                settingsViewModel = settingsViewModel,
                navController = navController,
                modifier = modifier
            )
        }
        composable(
            AppDestinations.BARCODE_SCREEN.getRouteWithArguments(),
        ) {
            BarCodeScannRoute(
                navigateUp = { navController.navigateUp() },
                changeServer = {
                    serviceViewModel.changeServiceUrl(it)
                }
            )
        }
        composable(
            route = AppDestinations.SMOV_DETAIL_SCREEN.getRouteWithArguments(),
            arguments =
            AppDestinations.SMOV_DETAIL_SCREEN.path.map {
                navArgument(it.first, builder = it.second)
            } + AppDestinations.SMOV_DETAIL_SCREEN.arguments.map {
                navArgument(it.first, builder = it.second)
            }) { backStackEntry ->
            val smovId =
                backStackEntry.arguments?.getLong("smov_id")
                    ?: return@composable
            val smovName =
                backStackEntry.arguments?.getString("smov_name")
                    ?: return@composable

            println(backStackEntry.arguments)

            val serverUrl by serviceViewModel.serverUrl

            SmovDetailRouter(
                smovId,
                smovName,
                serverUrl,
                hiltViewModel()
            ) { navController.navigateUp() }

        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavHomePage(
    homeViewModel: HomeViewModel,
    serviceViewModel: ServiceViewModel,
    settingsViewModel: SettingsViewModel,
    navigationActions: AppNavigationActions,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val navHomeController: NavHostController = rememberNavController()
    val navBackStackEntry by navHomeController.currentBackStackEntryAsState()
    val coroutineScope = rememberCoroutineScope()
    val serviceSwitch = @Composable {
        ServiceSwitchRoute(
            serviceViewModel = serviceViewModel,
            navigationActions = navigationActions,
        )
    }
    val home = @Composable {
        HomeRoute(
            homeViewModel = homeViewModel,
            serviceViewModel = serviceViewModel,
            navigationActions = navigationActions,
            openSmovDetail = navigationActions.navigateToSmovDetail
        )
    }
    val settings = @Composable {
        SettingsRoute(
            navigateUp = { navController.navigateUp() },
            settingsViewModel = settingsViewModel
        )
    }

    val serverState by homeViewModel.serverUrl

    val screens = listOf(
        AppDestinations.HOME_SCREEN to home,
        AppDestinations.SERVICE_SWITCH_SCREEN to serviceSwitch,
        AppDestinations.SETTINGS_SCREEN to settings,
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier.height(0.dp),
            ) {
                Text(text = serverState)
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .animateContentSize()
            ) {
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.first.icon!!,
                                contentDescription = screen.first.destination
                            )
                        },
                        label = { Text(screen.first.destination) },
                        selected = navBackStackEntry?.destination?.route == screen.first.route,
                        onClick = {
                            println(navBackStackEntry)
                            coroutineScope.launch {
                                navHomeController.navigate(screen.first.route) {
                                    popUpTo(navHomeController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navHomeController,
            startDestination = AppDestinations.HOME_SCREEN.getRouteWithArguments(),
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(
                    animationSpec = tween(500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(500)
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(500)
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(500)
                )
            }
        ) {
            for (screen in screens) {
                composable(screen.first.getRouteWithArguments()) {
                    screen.second()
                }
            }
        }


    }
}
