package com.leri.smovbook.ui

import androidx.compose.runtime.Immutable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object AppDestinations {
    const val HOME_ROUTE = "home_screen"
    const val BARCODE_ROUTE = "barcode_screen"
    const val SPLASH_SCREEN = "splash_screen"
    const val SMOV_DETAIL = "smov_detail"
    const val SETTINGS = "settings"

    const val SMOV_DETAIL_ARGUMENT0 = "smov_id"
    const val SMOV_DETAIL_ARGUMENT1 = "smov_name"
    const val SMOV_DETAIL_WITH_ARGUMENT = "smov_detail/{$SMOV_DETAIL_ARGUMENT0}?$SMOV_DETAIL_ARGUMENT1={$SMOV_DETAIL_ARGUMENT1}"
}

class AppNavigationActions(navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.popBackStack()
        navController.navigate(AppDestinations.HOME_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToBarCode: () -> Unit = {
        navController.navigate(AppDestinations.BARCODE_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToSettings: () -> Unit = {
        navController.navigate(AppDestinations.SETTINGS) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToSmovDetail: (smovId: Long, smovName: String) -> Unit = { smovId, smovName ->
        navController.navigate("${AppDestinations.SMOV_DETAIL}/$smovId?${AppDestinations.SMOV_DETAIL_ARGUMENT1}=$smovName") {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToSplashScreen: () -> Unit = {
        navController.navigate(AppDestinations.SPLASH_SCREEN) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

