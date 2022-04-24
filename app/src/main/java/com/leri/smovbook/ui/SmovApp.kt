package com.leri.smovbook.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.leri.smovbook.data.AppContainer
import com.leri.smovbook.ui.theme.SmovBookMTheme
import kotlinx.coroutines.launch
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import com.leri.smovbook.ui.components.AppScaffold
import com.leri.smovbook.ui.home.BackPressHandler


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmovApp(
    appContainer: AppContainer
) {
    SmovBookMTheme {
        val systemUiController = rememberSystemUiController()
        val darkIcons = MaterialTheme.colors.isLight
        SideEffect {
            systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = darkIcons)
        }

        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            AppNavigationActions(navController)
        }

        val coroutineScope = rememberCoroutineScope()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute =
            navBackStackEntry?.destination?.route ?: AppDestinations.HOME_ROUTE

        val drawerState = rememberDrawerState(initialValue = Closed)

        if (drawerState.isOpen) {
            BackPressHandler {
                coroutineScope.launch {
                    drawerState.close()
                }
            }
        }

        AppScaffold(
            drawerState = drawerState,
            currentRoute = currentRoute,
            navigateToHome = navigationActions.navigateToHome,
            closeDrawer = { coroutineScope.launch { drawerState.close() } },
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()

        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .windowInsetsPadding(
                        WindowInsets
                            .navigationBars
                            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                    )
            ) {
                AppNavGraph(
                    appContainer = appContainer,
                    navController = navController,
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                )
            }
        }
    }
}