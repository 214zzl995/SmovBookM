package com.leri.smovbook.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.leri.smovbook.R
import com.leri.smovbook.model.Actor
import com.leri.smovbook.model.Smov
import com.leri.smovbook.model.Tag
import com.leri.smovbook.model.SmovItem
import com.leri.smovbook.ui.FunctionalityNotAvailablePopup
import com.leri.smovbook.ui.components.SmovAppBar
import com.leri.smovbook.ui.theme.SmovBookMTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit = { },
    onErrorDismiss: (Long) -> Unit,
    uiState: HomeUiState,
    onRefreshSmovData: () -> Unit = { },
    homeListLazyListState: LazyListState,
    scaffoldState: ScaffoldState,
    openBarScann: () -> Unit = { },
) {

    val scrollState = rememberLazyListState()
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }

    Surface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                HomeScreenWithList(
                    uiState = uiState,
                    showTopAppBar = true,
                    onRefreshSmovData = onRefreshSmovData,
                    onErrorDismiss = onErrorDismiss,
                    openDrawer = openDrawer,
                    homeListLazyListState = homeListLazyListState,
                    openBarScann = openBarScann,
                    scaffoldState = scaffoldState
                ) { hasData, _ ->
                    SmovList(
                        smov = hasData.smov,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        scrollState = scrollState
                    )
                }
            }

        }
    }
}

@Composable
private fun HomeScreenWithList(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    onErrorDismiss: (Long) -> Unit,
    onRefreshSmovData: () -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    openBarScann: () -> Unit,
    hasPostsContent: @Composable (
        uiState: HomeUiState.HasData,
        modifier: Modifier
    ) -> Unit
) {
    androidx.compose.material.Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (showTopAppBar) {
                ChannelNameBar(
                    channelName = "SmovBook",
                    onNavIconPressed = openDrawer,
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.statusBarsPadding(),
                    onRefreshSmovData = onRefreshSmovData,
                    onOpenBarScann = openBarScann,
                    uiState = uiState
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        LoadingContent(
            empty = when (uiState) {
                is HomeUiState.HasData -> false
                is HomeUiState.NoData -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            loading = uiState.isLoading,
            onRefresh = onRefreshSmovData,
            content = {
                when (uiState) {
                    is HomeUiState.HasData -> hasPostsContent(uiState, contentModifier)
                    is HomeUiState.NoData -> {
                        if (uiState.errorMessages.isEmpty()) {
                            TextButton(
                                onClick = onRefreshSmovData,
                                modifier.fillMaxSize()
                            ) {
                                Text(
                                    stringResource(id = R.string.home_tap_to_load_content),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Box(contentModifier.fillMaxSize()) { }
                        }
                    }
                }
            }
        )
    }


    if (uiState.errorMessages.isNotEmpty()) {

        val errorMessage = remember(uiState) { uiState.errorMessages[0] }
        val errorMessageText: String = stringResource(errorMessage.messageId)
        val retryMessageText = stringResource(id = R.string.retry)
        val onRefreshSmovDataState by rememberUpdatedState(onRefreshSmovData)
        val onErrorDismissState by rememberUpdatedState(onErrorDismiss)

        LaunchedEffect(errorMessageText, retryMessageText, scaffoldState) {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessageText,
                actionLabel = retryMessageText
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                onRefreshSmovDataState()
            }
            onErrorDismissState(errorMessage.id)
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ChannelNameBar(
    channelName: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    onRefreshSmovData: () -> Unit = { },
    onOpenBarScann: () -> Unit = { },
    uiState: HomeUiState
) {
    var functionalityNotAvailablePopupShown by remember { mutableStateOf(false) }
    if (functionalityNotAvailablePopupShown) {
        FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
    }

    SmovAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        onNavIconPressed = onNavIconPressed,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = channelName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = uiState.serverUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            Icon(
                painter = painterResource(id = R.drawable.ic_qr_scan_line),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = { onOpenBarScann() })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(21.dp),
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
        }
    )
}

@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeRefresh(
            state = rememberSwipeRefreshState(loading),
            onRefresh = onRefresh,
            content = content,
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    scale = true,
                    modifier = Modifier.offset(y = 90.dp)
                )
            }
        )
    }
}

@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun SmovList(
    smov: Smov,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {

        LazyColumn(
            reverseLayout = false,
            state = scrollState,
            contentPadding =
            WindowInsets.statusBars.add(WindowInsets(top = 90.dp)).asPaddingValues(),
            modifier = Modifier
                .testTag("ConversationTestTag")
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            item {
                FlowRow(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),

                    mainAxisAlignment = FlowMainAxisAlignment.Center,
                    crossAxisAlignment = FlowCrossAxisAlignment.Center,
                ) {
                    for (smovItem in smov.smovList) {
                        SmovCard(smovItem)
                    }
                }
            }
        }
        val jumpThreshold = with(LocalDensity.current) {
            JumpToBottomThreshold.toPx()
        }

        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        JumpToTop(
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }

}

@Composable
fun DayHeader(dayString: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(16.dp)
    ) {
        DayHeaderLine()
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DayHeaderLine()
    }
}

@Composable
private fun RowScope.DayHeaderLine() {
    // TODO (M3): No Divider, replace when available
    Divider(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

@Preview
@Composable
fun Screen() {
    SmovBookMTheme() {
        HomeScreen(
            onErrorDismiss = {},
            uiState = HomeUiState.HasData(
                smov = Smov(
                    listOf(), SmovItem(
                        0,
                        "test",
                        "test",
                        "test",
                        "test",
                        10010,
                        4564541,
                        4564654,
                        "mp4",
                        "test",
                        "test",
                        123,
                        "test",
                        3,
                        "test",
                        1,
                        "test",
                        1,
                        "test",
                        1,
                        listOf(Tag(1, "test")),
                        listOf(Actor(1, "test")),
                        isch = false,
                        "https://z4a.net/images/2022/04/16/wallhaven-1kq1jg.jpg",
                        "https://pc-index-skin.cdn.bcebos.com/616ca57261c714fceccb1717f6911516.jpg",
                        listOf("https://pc-index-skin.cdn.bcebos.com/616ca57261c714fceccb1717f6911516.jpg")
                    )
                ),
                isLoading = false,
                errorMessages = listOf(),
                searchInput = "",
                serverUrl = "127.0.0.1",
                isDetailOpen = false,
                selectedSmov = SmovItem(
                    0,
                    "test",
                    "test",
                    "test",
                    "test",
                    10010,
                    4564541,
                    4564654,
                    "mp4",
                    "test",
                    "test",
                    123,
                    "test",
                    3,
                    "test",
                    1,
                    "test",
                    1,
                    "test",
                    1,
                    listOf(Tag(1, "test")),
                    listOf(Actor(1, "test")),
                    isch = false,
                    "https://z4a.net/images/2022/04/16/wallhaven-1kq1jg.jpg",
                    "https://pc-index-skin.cdn.bcebos.com/616ca57261c714fceccb1717f6911516.jpg",
                    listOf("https://pc-index-skin.cdn.bcebos.com/616ca57261c714fceccb1717f6911516.jpg")
                )
            ),
            homeListLazyListState = rememberLazyListState(0),
            scaffoldState = rememberScaffoldState()
        )
    }

}


@Preview
@Composable
fun ChannelBarPrev() {
    SmovBookMTheme {
        ChannelNameBar(
            channelName = "composers",
            uiState = HomeUiState.HasData(
                smov = Smov(
                    listOf(), SmovItem(
                        0,
                        "test",
                        "test",
                        "test",
                        "test",
                        10010,
                        4564541,
                        4564654,
                        "mp4",
                        "test",
                        "test",
                        123,
                        "test",
                        3,
                        "test",
                        1,
                        "test",
                        1,
                        "test",
                        1,
                        listOf(Tag(1, "test")),
                        listOf(Actor(1, "test")),
                        isch = false,
                        "https://z4a.net/images/2022/04/16/wallhaven-1kq1jg.jpg",
                        "https://pc-index-skin.cdn.bcebos.com/616ca57261c714fceccb1717f6911516.jpg",
                        listOf("https://pc-index-skin.cdn.bcebos.com/616ca57261c714fceccb1717f6911516.jpg")
                    )
                ),
                isLoading = false,
                errorMessages = listOf(),
                searchInput = "",
                serverUrl = "127.0.0.1",
                isDetailOpen = false,
                selectedSmov = SmovItem(
                    0,
                    "test",
                    "test",
                    "test",
                    "test",
                    10010,
                    4564541,
                    4564654,
                    "mp4",
                    "test",
                    "test",
                    123,
                    "test",
                    3,
                    "test",
                    1,
                    "test",
                    1,
                    "test",
                    1,
                    listOf(Tag(1, "test")),
                    listOf(Actor(1, "test")),
                    isch = false,
                    "https://z4a.net/images/2022/04/16/wallhaven-1kq1jg.jpg",
                    "https://pc-index-skin.cdn.bcebos.com/616ca57261c714fceccb1717f6911516.jpg",
                    listOf("https://pc-index-skin.cdn.bcebos.com/616ca57261c714fceccb1717f6911516.jpg")
                )
            )
        )
    }
}

@Preview
@Composable
fun DayHeaderPrev() {
    DayHeader("Aug 6")
}

private val JumpToBottomThreshold = 56.dp


