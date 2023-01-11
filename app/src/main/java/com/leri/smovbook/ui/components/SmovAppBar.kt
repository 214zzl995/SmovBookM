package com.leri.smovbook.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leri.smovbook.R
import com.leri.smovbook.ui.FunctionalityNotAvailablePopup
import com.leri.smovbook.ui.home.ServerState
import com.leri.smovbook.ui.theme.SmovBookMTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelNameBar(
    channelName: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    onRefreshSmovData: () -> Unit = { },
    onOpenBarScann: () -> Unit = { },
    serverState: ServerState,
) {
    var functionalityNotAvailablePopupShown by remember { mutableStateOf(false) }
    if (functionalityNotAvailablePopupShown) {
        FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
    }

    var serverSelectShow by remember { mutableStateOf(false) }

    val foregroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        scrolledContainerColor = MaterialTheme.colorScheme.inverseOnSurface
    )

    Column(modifier = Modifier) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            actions = {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clickable(onClick = {
                            onOpenBarScann()
                        })
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .height(24.dp),
                    contentDescription = stringResource(id = R.string.search)
                )

                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clickable(onClick = { onRefreshSmovData() })
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .height(24.dp),
                    contentDescription = stringResource(id = R.string.info)
                )
            },
            title = {
                Row(
                    modifier = Modifier.clickable(indication = null, interactionSource = remember {
                        MutableInteractionSource()
                    }) {
                        //打开地址选择栏
                        serverSelectShow = !serverSelectShow
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = channelName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(
                            modifier = Modifier.height(15.dp)
                        ) {
                            Text(
                                text = serverState.serverUrl,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                modifier = Modifier.fillMaxHeight(),
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = stringResource(id = R.string.server_select),
                                tint = if (serverSelectShow) Color.Red else Color.Blue
                            )
                        }

                    }


                }

            },
            scrollBehavior = scrollBehavior,
            colors = foregroundColors,
            navigationIcon = {
                BarIcon(
                    contentDescription = stringResource(id = R.string.navigation_drawer_open),
                    modifier = Modifier
                        .size(64.dp)
                        .clickable(onClick = onNavIconPressed)
                        .padding(21.dp)
                )
            }
        )

        BackHandler(
            onBack = {
                serverSelectShow = !serverSelectShow
            },
            enabled = serverSelectShow
        )

        AnimatedVisibility(
            visible = serverSelectShow,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            ServerSelect(
                backgroundColor = if ((scrollBehavior?.state?.overlappedFraction
                        ?: 0f) > 0.01f
                ) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.surface,
                serverState = serverState
            )
        }


    }

}

@Composable
fun ServerSelect(
    backgroundColor: Color,
    serverState: ServerState,
) {

    Surface(
        modifier = Modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier
            .fillMaxHeight(0.9f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp))
            .background(backgroundColor)

        ) {
            Text(serverState.serverUrl)
        }

    }
}

@Preview
@Composable
fun ServerSelectPreview() {
    SmovBookMTheme(isDarkTheme = false) {
        ServerSelect(backgroundColor = MaterialTheme.colorScheme.surface,
            serverState = ServerState("127.0.0.1"))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppBarPreview() {
    SmovBookMTheme(isDarkTheme = false) {
        ChannelNameBar(channelName = "SmovBook", serverState = ServerState("127.0.0.1"))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppBarPreviewDark() {
    SmovBookMTheme(isDarkTheme = true) {
        ChannelNameBar(channelName = "SmovBook", serverState = ServerState("127.0.0.1"))
    }
}
