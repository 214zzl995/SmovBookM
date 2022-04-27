package com.leri.smovbook.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalContext
import com.leri.smovbook.ui.components.AppScaffold
import com.leri.smovbook.ui.home.BackPressHandler
import kotlin.system.exitProcess


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

        Row(
            Modifier
                .fillMaxSize()
                //.statusBarsPadding()设置一个 导航栏的上padding
                .windowInsetsPadding(
                    WindowInsets
                        .navigationBars
                        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                )
        ) {
            AppNavGraph(
                appContainer = appContainer,
                navController = navController,
            )
        }
    }
}