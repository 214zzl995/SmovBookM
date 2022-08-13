package com.leri.smovbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leri.smovbook.ui.data.testDataHasData
import com.leri.smovbook.ui.home.HomeUiState
import com.leri.smovbook.ui.refactor.Refactor
import com.leri.smovbook.ui.theme.SmovBookMTheme
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ColumnScope.AppDrawer(
    currentRoute: String,
    uiState: HomeUiState,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
    DrawerHeader()
    DividerItem()
    DrawerItemHeader("Chats")
    for (his in uiState.historyUrl) {
        Text(text = his)

    }
    Text(text = "fuck")
    Refactor(hiltViewModel())
    DividerItem(modifier = Modifier.padding(horizontal = 28.dp))
    DrawerItemHeader("Recent Profiles")



}

@Composable
private fun DrawerHeader() {
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = CenterVertically) {
        BarIcon(
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun DrawerItemHeader(text: String) {
    Box(
        modifier = Modifier
            .heightIn(min = 52.dp)
            .padding(horizontal = 28.dp),
        contentAlignment = CenterStart
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun DividerItem(modifier: Modifier = Modifier) {
    // TODO (M3): No Divider, replace when available
    Divider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

@Composable
@Preview
fun DrawerPreview() {
    SmovBookMTheme() {
        Surface {
            Column {
                AppDrawer("SmovBook", uiState = testDataHasData, {})
            }
        }
    }
}

@Composable
@Preview
fun DrawerPreviewDark() {
    SmovBookMTheme(isDarkTheme = true) {
        Surface {
            Column {
                AppDrawer("SmovBook", uiState = testDataHasData, {})
            }
        }
    }
}


