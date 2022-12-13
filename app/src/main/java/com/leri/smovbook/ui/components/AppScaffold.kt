package com.leri.smovbook.ui.components

import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = Closed),
    currentRoute: String,
    historyUrl: MutableList<String>,
    serverUrl: String,
    closeDrawer: () -> Unit,
    changeServerUrl: (String) -> Unit,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                AppDrawer(
                    currentRoute = currentRoute,
                    closeDrawer = closeDrawer,
                    historyUrl = historyUrl,
                    modifier = modifier,
                    serverUrl = serverUrl,
                    drawerState = drawerState,
                    changeServerUrl = changeServerUrl
                )
            }
        },
        content = content
    )
}

