package com.leri.smovbook.ui.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leri.smovbook.R
import com.leri.smovbook.config.ThirdPartyPlayer
import com.leri.smovbook.ui.theme.SmovBookMTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    thirdPartyPlayer: ThirdPartyPlayer,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "设置",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    Row(modifier = Modifier.padding(start = 11.dp)) {
                        Icon(
                            modifier = Modifier.height(24.dp),
                            imageVector = Icons.Outlined.Settings,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = stringResource(id = R.string.info)
                        )
                    }
                },
                actions = {},
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SettingsTitle(text = "系统")
                }
                item {
                    SettingsItem(
                        text = "默认打开应用",
                        description = thirdPartyPlayer.thirdPartyPlayerName,
                        icon = Icons.Outlined.Android,
                        onClick = {}
                    )
                }
            }
        }
    )
}

@Composable
fun SettingsTitle(
    text: String,
) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, bottom = 15.dp),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun SettingsItem(
    text: String,
    icon: ImageVector,
    description: String = "",
    onClick: () -> Unit,
    action: @Composable () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = text, Modifier.size(35.dp))
            Column(modifier = Modifier.padding(start = 20.dp)) {
                Text(
                    text = text,
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (description != "") {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

            }
        }

        Box(modifier = Modifier) {
            action()
        }

    }


}


@Preview
@Composable
fun BarCodeScanTitlePreview() {
    SmovBookMTheme() {
        Surface {
            Column {
                SettingsItem(
                    text = "默认打开应用",
                    description = "某某应用",
                    icon = Icons.Outlined.Android,
                    onClick = {}
                ) {
                    Switch(checked = false, onCheckedChange = {})
                }
            }
        }
    }
}

@Preview
@Composable
fun BarCodeScanTitleUndPreview() {
    SmovBookMTheme() {
        Surface {
            Column {
                SettingsItem(
                    text = "默认打开应用",
                    icon = Icons.Outlined.Android,
                    onClick = {}
                ) {
                    Switch(checked = false, onCheckedChange = {})
                }
            }
        }
    }
}



