package com.leri.smovbook.ui.serviceSwitch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AllInclusive
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leri.smovbook.R
import com.leri.smovbook.ui.theme.SmovBookMTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceSwitchScreen(
    modifier: Modifier = Modifier,
    serviceUrl: String,
    historyUrl: List<String>,
    openBarScann: () -> Unit,
    changeServiceUrl: (String) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "服务",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    Row (modifier = Modifier.padding(start = 13.dp)) {
                        Icon(
                            modifier = Modifier.height(24.dp),
                            imageVector = Icons.Outlined.AllInclusive,
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
                modifier = Modifier.padding(all = 16.dp),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(count = historyUrl.size) {
                    Url(
                        url = historyUrl[it],
                        check = serviceUrl == checkPortJoin(historyUrl[it]),
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember {
                                MutableInteractionSource()
                            }) {
                            changeServiceUrl(historyUrl[it])
                        },
                    )
                }

            }
        },
        floatingActionButton = {
            AddUrlFloatingActionButton(
                openBarScann = openBarScann,
                openEditInput = {})
        }
    )
}

@Composable
private fun Url(
    url: String,
    check: Boolean,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (check) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Box(
        contentAlignment = Alignment.Center,

        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, borderColor, MaterialTheme.shapes.medium)
            .padding(vertical = 16.dp)
    ) {

        Text(
            text = url,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        //尾部显示确认图标
        if (check) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = stringResource(id = R.string.content_description),
                modifier = Modifier
                    .height(21.dp)
                    .padding(end = 16.dp)
                    .align(Alignment.CenterEnd),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

}

@Composable
private fun AddUrlFloatingActionButton(
    openBarScann: () -> Unit,
    openEditInput: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.End

    ) {
        ExtendedFloatingActionButton(
            onClick = {
                openBarScann()
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_qr_scan_line),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .height(21.dp),
                    contentDescription = stringResource(id = R.string.search)
                )
            },
            text = {
                Text(text = "扫码")
            },
        )
        Box(modifier = Modifier.height(10.dp)) {

        }

        ExtendedFloatingActionButton(
            onClick = { openEditInput() },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_enter_the_keyboard),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .height(21.dp),
                    contentDescription = stringResource(id = R.string.search)
                )
            },
            text = {
                Text(text = "输入")
            },
        )
    }

}


fun checkPortJoin(url: String): String {
    val urlList = url.split(":")
    if (urlList.size == 1) {
        return urlList[0] + ":80"
    }
    return url
}

@Preview
@Composable
fun UrlPreview() {
    SmovBookMTheme(isDarkTheme = false) {
        Url(
            url = "https://www.baidu.com",
            check = false
        )
    }
}

@Preview
@Composable
fun CheckUrlPreview() {
    SmovBookMTheme(isDarkTheme = false) {
        Url(
            url = "https://www.baidu.com",
            check = true
        )
    }
}

@Preview
@Composable
fun AddUrlFloatingActionButtonPreview() {
    SmovBookMTheme(isDarkTheme = false) {
        AddUrlFloatingActionButton(
            openBarScann = {},
            openEditInput = {})
    }
}
