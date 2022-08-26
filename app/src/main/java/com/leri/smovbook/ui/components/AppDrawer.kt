package com.leri.smovbook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leri.smovbook.ui.clearFocusOnKeyboardDismiss
import com.leri.smovbook.ui.theme.SmovBookMTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.AppDrawer(
    currentRoute: String,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    historyUrl: MutableList<String>,
    serverUrl: String,
    changeServerUrl: (String) -> Unit,
) {
    Column(modifier = Modifier) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        DrawerHeader()
        DividerItem()
        DrawerItemHeader("手动新增")
        AddUrl(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
            changeServerUrl = changeServerUrl,
            drawerState = drawerState
        )

        DrawerItemHeader("当前")
        Text(serverUrl, modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp))
        HistoryUrl(historyUrl = historyUrl, changeServerUrl = changeServerUrl, closeDrawer = closeDrawer)
    }


}

@Composable
private fun DrawerHeader() {
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = CenterVertically) {
        BarIcon(
            contentDescription = null, modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun HistoryUrl(
    modifier: Modifier = Modifier,
    historyUrl: MutableList<String>,
    changeServerUrl: (String) -> Unit,
    closeDrawer: () -> Unit,
) {
    Column(modifier = modifier) {
        DrawerItemHeader("历史")
        for (historyUrlItem in historyUrl) {
            Text(historyUrlItem, modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp).clickable {
                changeServerUrl(historyUrlItem)
                closeDrawer()
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddUrl(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    changeServerUrl: (String) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }

    Column(modifier = modifier) {

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("输入新的url") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().clearFocusOnKeyboardDismiss().focusable(drawerState.isOpen),
        )
        Button(onClick = { changeServerUrl(text) }, modifier = Modifier) { Text("缓存") }
    }
}

@Composable
private fun SmovUrl(
    modifier: Modifier = Modifier,
    url: String
) {


}

@Composable
private fun DrawerItemHeader(text: String) {

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 20.dp),
        verticalAlignment = CenterVertically
    ) {
        Text(
            text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp)
        )
        DividerItem(modifier = Modifier)
    }
}


@Composable
fun DividerItem(modifier: Modifier = Modifier) {
    // TODO (M3): No Divider, replace when available
    Divider(
        modifier = modifier, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun DrawerPreview() {
    SmovBookMTheme() {
        Surface {
            Column {
                AppDrawer(
                    "SmovBook",
                    {},
                    historyUrl = mutableListOf("122.22.22.22:145", "122.22.22.22:145", "122.22.22.22:145"),
                    changeServerUrl = {},
                    serverUrl = "127.0.0.1:8000"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun DrawerPreviewDark() {
    SmovBookMTheme(isDarkTheme = true) {
        Surface {
            Column {
                AppDrawer(
                    "SmovBook",
                    {},
                    historyUrl = mutableListOf(),
                    changeServerUrl = {},
                    serverUrl = "127.0.0.1:8000"
                )
            }
        }
    }
}






