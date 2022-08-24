package com.leri.smovbook.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.leri.smovbook.data.AppContainer
import com.leri.smovbook.ui.barcodeScann.BarCodeScannRoute
import com.leri.smovbook.ui.home.HomeRoute
import com.leri.smovbook.ui.home.HomeViewModel

@Composable
fun AppNavGraph(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestinations.HOME_ROUTE
) {

    val homeViewModel: HomeViewModel by hiltViewModel()
    val navigationActions = remember(navController) {
        AppNavigationActions(navController)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute =
        navBackStackEntry?.destination?.route ?: AppDestinations.HOME_ROUTE

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(AppDestinations.HOME_ROUTE) {

            HomeRoute(
                homeViewModel = homeViewModel,
                openBarScann = navigationActions.navigateToBarCode,
                currentRoute = currentRoute
            )
        }
        composable(AppDestinations.BARCODE_ROUTE) {
            BarCodeScannRoute(
                navigateUp = { navController.navigateUp() },
                changeServer = {
                    homeViewModel.changeServerUrl(it)
                }
            )
        }
    }
}