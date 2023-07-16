package com.leri.smovbook.ui.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leri.smovbook.ui.util.clearFocusOnKeyboardDismiss
import com.leri.smovbook.ui.home.ServerState
import com.leri.smovbook.ui.theme.SmovBookMTheme


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AppDrawer(
    currentRoute: String,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    serverState: ServerState,
) {
    Column(modifier = Modifier) {
       /* Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBarsIgnoringVisibility))
        DrawerHeader()
        DividerItem()

        DrawerItemHeader("当前")
        SmovUrl(url = serverState.serverUrl, modifier = Modifier, changeServerUrl = {})
        HistoryUrl(
            historyUrl = serverState.historyUrl,
            changeServerUrl = serverState.changeServerUrl,
            closeDrawer = closeDrawer
        )*/
        DrawerHeader()
        DividerItem()

        FullScreenLoading()

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
    LazyColumn {
        item {
            DrawerItemHeader("历史")
        }
        items(historyUrl) {
            SmovUrl(
                url = it,
                modifier = Modifier.animateItemPlacement(),
                changeServerUrl = {
                    changeServerUrl(it)
                    closeDrawer()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun AddUrl(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    changeServerUrl: (String) -> Unit,
) {
    var text by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier) {
        //此处 keyboardActions keyboardOptions的意思是 当ime发出 enter的指令后 隐藏键盘
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("输入新的url") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .clearFocusOnKeyboardDismiss()
                .focusable(drawerState.isOpen),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        changeServerUrl(text)
                        text = ""
                        keyboardController?.hide()
                    },
                    enabled = text != ""
                ) {
                    val visibilityIcon = Icons.TwoTone.Check

                    Icon(imageVector = visibilityIcon, contentDescription = "缓存")
                }
            }
        )
    }
}


@Composable
private fun DrawerItemHeader(text: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 20.dp),
        verticalAlignment = CenterVertically
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp)
        )
        DividerItem(modifier = Modifier.padding(start = 5.dp))
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
                    serverState = ServerState(
                        "127.0.0.1:8000", mutableListOf(
                            "122.22.22.22:145",
                            "122.22.22.22:145",
                            "122.22.22.22:145"
                        )
                    )
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
                    serverState = ServerState("127.0.0.1:8000", mutableListOf())
                )
            }
        }
    }
}






