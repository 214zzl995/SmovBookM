package com.leri.smovbook.ui.components

import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.leri.smovbook.ui.home.ServerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = Closed),
    currentRoute: String,
    closeDrawer: () -> Unit,
    serverState: ServerState,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                AppDrawer(
                    currentRoute = currentRoute,
                    closeDrawer = closeDrawer,
                    modifier = modifier,
                    drawerState = drawerState,
                    serverState = serverState
                )
            }
        },
        content = content
    )
}

