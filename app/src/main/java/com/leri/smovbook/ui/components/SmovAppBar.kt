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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
            BarIcon(
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

        AnimatedVisibility(
            visible = serverSelectShow,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
        }
        ServerSelect(
            backgroundColor = if ((scrollBehavior?.state?.overlappedFraction
                    ?: 0f) > 0.01f
            ) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
            serverState = serverState,
            serverSelectShow = serverSelectShow
        ) { serverSelectShow = !serverSelectShow }

    }
}

@OptIn(InternalAnimationApi::class)
@Composable
fun ServerSelect(
    backgroundColor: Color,
    serverState: ServerState,
    serverSelectShow: Boolean = false,
    changeServerSelectState: () -> Unit = {},
) {
    val localDensity = LocalDensity.current

    val transition = updateTransition(serverSelectShow, "changeUrlBox")

    val changeBoxBackground by animateColorAsState(
        targetValue = if (serverSelectShow) ChangeServerUrlBackground else Color.Transparent,
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        ),
        label = "changeUrlBox"
    )

    var changeBoxHeightDp by remember {
        mutableStateOf(0f)
    }

    println("第一次测量的高度为$changeBoxHeightDp")

    val changeBoxOffset: Float by animateFloatAsState(
        targetValue = if (serverSelectShow) 0f else changeBoxHeightDp,
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        ),
        label = "changeUrlBox"
    )

    if (transition.isSeeking) {
        Surface(
            color = Color.Transparent,
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(changeBoxBackground)
                .clickable(indication = null, interactionSource = remember {
                    MutableInteractionSource()
                }) {
                    changeServerSelectState()
                }) {

                Box(modifier = Modifier
                    .fillMaxHeight(0.55f)
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        changeBoxHeightDp =
                            with(localDensity) { -coordinates.size.height.toDp().value }
                    }
                    .offset(x = 0.dp, y = (changeBoxOffset).dp)
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
