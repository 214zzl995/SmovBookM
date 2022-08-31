package com.leri.smovbook.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.leri.smovbook.ui.theme.SmovBookMTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SmovApp(
    modifier: Modifier = Modifier
) {
    SmovBookMTheme {
        val systemUiController = rememberSystemUiController()
        val darkIcons = MaterialTheme.colors.isLight
        SideEffect {
            systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = darkIcons)
        }

        val navController = rememberAnimatedNavController()

        Row(
            modifier
                .fillMaxSize()
                //.statusBarsPadding()设置一个 导航栏的上padding
                .windowInsetsPadding(
                    WindowInsets
                        .navigationBars
                        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                )
        ) {
            AppNavGraph(
                navController = navController,
            )
        }
    }
}