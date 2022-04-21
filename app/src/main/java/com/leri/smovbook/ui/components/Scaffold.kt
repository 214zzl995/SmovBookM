package com.leri.smovbook.ui.components

import android.graphics.Rect
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.leri.smovbook.ui.theme.SmovBookMTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Scaffold(
    drawerState: DrawerState = rememberDrawerState(initialValue = Closed),
    onProfileClicked: (String) -> Unit,
    onChatClicked: (String) -> Unit,
    content: @Composable () -> Unit
) {
    SmovBookMTheme() {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Drawer(
                    onProfileClicked = onProfileClicked,
                    onChatClicked = onChatClicked
                )
            },
            content = content,
            drawerShape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 0.dp),
        )
    }
}

