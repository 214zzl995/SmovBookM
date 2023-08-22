package com.leri.smovbook.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.leri.smovbook.R
import com.leri.smovbook.ui.FunctionalityNotAvailablePopup
import com.leri.smovbook.ui.home.ServerState
import com.leri.smovbook.ui.theme.ChangeServerUrlBackground
import com.leri.smovbook.ui.theme.Shapes
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
    onOpenSettings:() -> Unit = { },
    serverState: ServerState,
) {
    var functionalityNotAvailablePopupShown by remember { mutableStateOf(false) }
    if (functionalityNotAvailablePopupShown) {
        FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
    }

    var serverSelectShow by remember { mutableStateOf(false) }

    val foregroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer
    )

    Column(modifier = Modifier) {

        CenterAlignedTopAppBar(modifier = modifier, actions = {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = { onRefreshSmovData() })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = stringResource(id = R.string.info)
            )
        }, title = {
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
                        text = channelName, style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        modifier = Modifier.height(15.dp)
                    ) {
                        Text(
                            text = serverState.serverUrl,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Blue
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
        }, scrollBehavior = scrollBehavior, colors = foregroundColors, navigationIcon = {
            if (false) BarIcon(
                contentDescription = stringResource(id = R.string.navigation_drawer_open),
                modifier = Modifier
                    .size(64.dp)
                    .clickable(onClick = onNavIconPressed)
                    .padding(21.dp)
            )


        })

        BackHandler(
            onBack = {
                serverSelectShow = !serverSelectShow
            }, enabled = serverSelectShow
        )

        ServerSelect(
            backgroundColor = if ((scrollBehavior?.state?.overlappedFraction
                    ?: 0f) > 0.01f
            ) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
            serverState = serverState,
            toOpenBarScann = onOpenBarScann,
            serverSelectVisible = serverSelectShow
        ) { serverSelectShow = !serverSelectShow }

    }
}

@Composable
fun ServerSelect(
    backgroundColor: Color,
    serverState: ServerState,
    serverSelectVisible: Boolean = false,
    toOpenBarScann: () -> Unit = {},
    changeServerSelectState: () -> Unit = {},
) {
    AnimatedVisibility(visible = serverSelectVisible, enter = fadeIn(), exit = fadeOut()) {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.zIndex(9999f)
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(ChangeServerUrlBackground)
                .clickable(indication = null, interactionSource = remember {
                    MutableInteractionSource()
                }) {
                    changeServerSelectState()
                }) {
                Box(modifier = Modifier
                    .fillMaxHeight(0.55f)
                    .fillMaxWidth()
                    .animateEnterExit(
                        enter = slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    )
                    .clip(RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp))
                    .background(backgroundColor)
                    .pointerInput(Unit) {
                        detectTapGestures()
                    }) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 15.dp)
                    ) {
                        LazyColumn {
                            items(serverState.historyUrl) {
                                SmovUrl(
                                    url = it,
                                    modifier = Modifier.animateItemPlacement()
                                ) {
                                    serverState.changeServerUrl(it)
                                    changeServerSelectState()
                                }
                            }

                        }

                    }
                }
                //一个重叠的box 大小与上一个box相同 并且有红色边框
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.67f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp)),
                    contentAlignment = Alignment.BottomEnd,
                ) {
                    //将 FloatingActionButton 显示在 右下角
                    FloatingActionButton(
                        onClick = {
                            toOpenBarScann()
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding()
                            .fillMaxHeight(0.135f)
                            .width(70.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            modifier = Modifier
                                .height(24.dp),
                            contentDescription = null
                        )
                    }
                }

            }

        }
    }
}

@OptIn(InternalAnimationApi::class, ExperimentalAnimationApi::class)
@Composable
private fun <T> Transition<T>.targetEnterExit(
    visible: (T) -> Boolean,
    targetState: T,
): EnterExitState = key(this) {

    if (this.isSeeking) {
        if (visible(targetState)) {
            EnterExitState.Visible
        } else {
            if (visible(this.currentState)) {
                EnterExitState.PostExit
            } else {
                EnterExitState.PreEnter
            }
        }
    } else {
        val hasBeenVisible = remember { mutableStateOf(false) }
        if (visible(currentState)) {
            hasBeenVisible.value = true
        }
        if (visible(targetState)) {
            EnterExitState.Visible
        } else {
            // If never been visible, visible = false means PreEnter, otherwise PostExit
            if (hasBeenVisible.value) {
                EnterExitState.PostExit
            } else {
                EnterExitState.PreEnter
            }
        }
    }
}

@Composable
fun SmovUrl(
    modifier: Modifier = Modifier,
    url: String,
    textDecoration: TextDecoration? = null,
    changeServerUrl: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 2.dp)
            .height(35.dp)
            .clip(Shapes.small)
            .background(MaterialTheme.colorScheme.inversePrimary)
            .clickable { changeServerUrl() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            url,
            modifier = Modifier,
            textDecoration = textDecoration
        )

    }


}

@Preview
@Composable
fun ServerSelectPreview() {
    SmovBookMTheme(isDarkTheme = false) {
        ServerSelect(
            backgroundColor = MaterialTheme.colorScheme.surface,
            serverState = ServerState("127.0.0.1")
        )
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
