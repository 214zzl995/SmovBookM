package com.leri.smovbook.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
            AppDrawer(
                currentRoute = currentRoute,
                closeDrawer = closeDrawer,
                modifier = modifier,
                historyUrl = historyUrl,
                serverUrl = serverUrl,
                changeServerUrl = changeServerUrl
            )
        },
        content = content,
        drawerShape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 0.dp),
    )
}

