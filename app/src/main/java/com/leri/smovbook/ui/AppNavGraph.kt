package com.leri.smovbook.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.leri.smovbook.data.AppContainer
import com.leri.smovbook.ui.barcodeScann.BarCodeScannRoute
import com.leri.smovbook.ui.components.AppScaffold
import com.leri.smovbook.ui.home.HomeRoute
import com.leri.smovbook.ui.home.HomeViewModel
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestinations.HOME_ROUTE
) {
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(appContainer.smovRepository)
    )
    val navigationActions = remember(navController) {
        AppNavigationActions(navController)
    }
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(AppDestinations.HOME_ROUTE) {
            val context = LocalContext.current
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val currentRoute =
                navBackStackEntry?.destination?.route ?: AppDestinations.HOME_ROUTE

            var backTime by remember { mutableStateOf(System.currentTimeMillis() - 20000) }
            val coroutineScope = rememberCoroutineScope()

            //当抽屉开启时的监听
            BackHandler(
                onBack = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                enabled = drawerState.isOpen
            )

            //当抽屉关闭返回时的监听
            BackHandler(
                onBack = {
                    coroutineScope.launch {
                        val nowTime = System.currentTimeMillis();
                        if (nowTime - backTime > 2000) {
                            Toast.makeText(context, "再次返回退出程序", Toast.LENGTH_SHORT).show()
                            backTime = nowTime
                        } else {
                            exitProcess(1)
                        }
                    }
                },
                enabled = drawerState.isClosed
            )

            AppScaffold(
                drawerState = drawerState,
                currentRoute = currentRoute,
                navigateToHome = navigationActions.navigateToHome,
                closeDrawer = { coroutineScope.launch { drawerState.close() } },
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                HomeRoute(
                    openDrawer = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    },
                    homeViewModel = homeViewModel,
                    openBarScann = navigationActions.navigateToBarCode
                )
            }
        }
        composable(AppDestinations.BARCODE_ROUTE) {
            BarCodeScannRoute(
                navigateUp = { navController.navigateUp() },
                changeServer = {
                    homeViewModel.changeCacheServe(it)
                }
            )
        }
    }
}