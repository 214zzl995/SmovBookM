package com.leri.smovbook.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType

data class Route(
    val route: String,
    val destination: String,
    val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = emptyList(),
    val path: List<Pair<String, NavArgumentBuilder.() -> Unit>> = emptyList(),
    val icon: ImageVector? = null,
) {
    fun getRouteWithArguments(): String {
        val routeOut = if (path.isNotEmpty()) {
            val paths = path.joinToString(separator = "/") { "{${it.first}}" }
            "$route/$paths"
        } else {
            route
        }
        return if (arguments.isEmpty()) {
            routeOut
        } else {
            val args = arguments.joinToString(separator = "&") { "${it.first}={${it.first}}" }
            "$routeOut?$args"
        }
    }

    fun getRouteWithArguments(
        arguments: List<Pair<String, String>> = emptyList(),
        path: List<Pair<String, String>> = emptyList(),
    ): String {
        val routeOut = if (path.isNotEmpty()) {
            val paths = path.joinToString(separator = "/") { it.second }
            "$route/$paths"
        } else {
            route
        }
        return if (arguments.isEmpty()) {
            routeOut
        } else {
            val args = arguments.joinToString(separator = "&") { "${it.first}=${it.second}" }
            "$routeOut?$args"
        }
    }
}

object AppDestinations {
    val INDEX_SCREEN = Route("index", "首页")
    val HOME_SCREEN = Route("home", "首页", icon = Icons.Filled.Home)
    val SETTINGS_SCREEN = Route("settings", "设置", icon = Icons.Filled.Settings)
    val SERVICE_SWITCH_SCREEN =
        Route("service_switch", "服务", icon = Icons.Filled.AllInclusive)
    val BARCODE_SCREEN = Route("barcode", "扫码")
    val SPLASH_SCREEN = Route("splash", "闪屏")
    val SMOV_DETAIL_SCREEN = Route(
        "smov_detail",
        "详情",
        arguments = listOf("smov_name" to { defaultValue = "SmovBook" }),
        path = listOf("smov_id" to { type = NavType.LongType }),
    )
}

class AppNavigationActions(navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.popBackStack()
        navController.navigate(AppDestinations.HOME_SCREEN.getRouteWithArguments()) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToBarCode: () -> Unit = {
        navController.navigate(AppDestinations.BARCODE_SCREEN.getRouteWithArguments()) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToSettings: () -> Unit = {
        navController.navigate(AppDestinations.SETTINGS_SCREEN.getRouteWithArguments()) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToSmovDetail: (smovId: Long, smovName: String) -> Unit = { smovId, smovName ->

        val path = AppDestinations.SMOV_DETAIL_SCREEN.getRouteWithArguments(
            listOf("smov_name" to smovName),
            listOf("smov_id" to smovId.toString())
        )

        println(path)

        navController.navigate(path) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToSplashScreen: () -> Unit = {
        navController.navigate(AppDestinations.SPLASH_SCREEN.getRouteWithArguments()) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

