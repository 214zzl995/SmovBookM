package com.leri.smovbook.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.InputMode.Companion.Keyboard
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.KeyboardUtils
import com.leri.smovbook.ui.clearFocusOnKeyboardDismiss
import com.leri.smovbook.ui.theme.SmovBookMTheme
import com.leri.smovbook.ui.theme.scanBorder


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBarsIgnoringVisibility))
        DrawerHeader()
        DividerItem()

        DrawerItemHeader("当前")
        SmovUrl(url = serverUrl, modifier = Modifier)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HistoryUrl(
    modifier: Modifier = Modifier,
    historyUrl: MutableList<String>,
    changeServerUrl: (String) -> Unit,
    closeDrawer: () -> Unit,
) {
    val imeVisible = WindowInsets.isImeVisible
    Column(modifier = modifier) {
        DrawerItemHeader("历史")
        for (historyUrlItem in historyUrl) {
            SmovUrl(
                url = historyUrlItem,
                modifier = Modifier.clickable(enabled = !imeVisible) {
                    changeServerUrl(historyUrlItem)
                    closeDrawer()
                },
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun AddUrl(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    changeServerUrl: (String) -> Unit
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
            modifier = Modifier.fillMaxWidth().clearFocusOnKeyboardDismiss().focusable(drawerState.isOpen),
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
private fun SmovUrl(
    modifier: Modifier = Modifier,
    url: String,
    textDecoration: TextDecoration? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 2.dp).height(35.dp), verticalAlignment = CenterVertically
    ) {
        Text(
            url,
            textDecoration = textDecoration
        )
    }


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






