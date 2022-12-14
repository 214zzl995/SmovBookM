package com.leri.smovbook.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.leri.smovbook.R
import com.leri.smovbook.ui.FunctionalityNotAvailablePopup
import com.leri.smovbook.ui.components.JumpToTop
import com.leri.smovbook.ui.components.SmovAppBar
import com.leri.smovbook.ui.theme.SmovBookMTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import com.leri.smovbook.models.entities.Smov
import com.leri.smovbook.models.network.NetworkState
import com.leri.smovbook.models.network.isLoading
import com.leri.smovbook.ui.data.testDataHasData

//接下来要实现 可见 ScrollBar 不然数量一多就很麻烦
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
    serverUrl: String,
    loadingState: NetworkState,
    fetchNextSmovPage: () -> Unit,
    changeServerUrl: (String) -> Unit,
    openSmovDetail: (Long, String) -> Unit,
) {

    val scrollState = rememberLazyListState()
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }

    var changeServerUrlDialogVisible by remember { mutableStateOf(false) }

    Surface(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {

                HomeScreenWithList(
                    uiState = uiState,
                    onRefreshSmovData = onRefreshSmovData,
                    onErrorDismiss = onErrorDismiss,
                    openDrawer = openDrawer,
                    homeListLazyListState = homeListLazyListState,
                    openBarScann = { changeServerUrlDialogVisible = true },
                    scaffoldState = scaffoldState,
                    loadingState = loadingState,
                    serverUrl = serverUrl,
                    fetchNextSmovPage = fetchNextSmovPage
                ) { hasData ->
                    SmovList(
                        smov = hasData.smovs,
                        serverUrl = serverUrl,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        scrollState = scrollState,
                        openSmovDetail = openSmovDetail
                    )
                }
            }

        }
        ChangeServerUrlDialog(
            openBarScann,
            changeServerUrlDialogVisible,
            close = { changeServerUrlDialogVisible = false },
            changeServerUrl = changeServerUrl
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun HomeScreenWithList(
    uiState: HomeUiState,
    onErrorDismiss: (Long) -> Unit,
    onRefreshSmovData: () -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    openBarScann: () -> Unit,
    loadingState: NetworkState,
    serverUrl: String,
    fetchNextSmovPage: () -> Unit,
    hasPostsContent: @Composable (
        uiState: HomeUiState.HasData
    ) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (uiState.errorMessages.isEmpty() && !loadingState.isLoading() && uiState.errorMessages.count() > 100) {
                ExtendedFloatingActionButton(
                    onClick = onRefreshSmovData
                ) { Text("重载") }
            }
        },
    )
    { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        LoadingContent(
            empty = when (uiState) {
                is HomeUiState.HasData -> false
                is HomeUiState.NoData -> loadingState.isLoading()
            },
            emptyContent = { FullScreenLoading() },
            loading = loadingState.isLoading(),
            onRefresh = onRefreshSmovData,
            content = {
                when (uiState) {
                    is HomeUiState.HasData -> hasPostsContent(uiState)
                    is HomeUiState.NoData -> {
                        if (uiState.errorMessages.isEmpty()) {
                            Box(Modifier.fillMaxSize()) {
                                NodataOperate(
                                    onRefresh = onRefreshSmovData,
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                )
                            }

                        } else {
                            Box(contentModifier.fillMaxSize()) {

                            }
                        }
                    }
                }
            },
        )

        //这个text为了避免头部url没有重组 因为showTopAppBar 固定值 影响了页面更新重组
        Text(text = serverUrl)

        val contentPadding = WindowInsets.statusBarsIgnoringVisibility.asPaddingValues()
        ChannelNameBar(
            channelName = "SmovBook",
            onNavIconPressed = openDrawer,
            scrollBehavior = scrollBehavior,
            //modifier = Modifier.statusBarsPadding(),
            modifier = Modifier.padding(contentPadding),
            onRefreshSmovData = onRefreshSmovData,
            onOpenBarScann = openBarScann,
            serverUrl = serverUrl
        )

        //这个text为了避免头部url没有重组 因为showTopAppBar 固定值 影响了页面更新重组
        //Text(text = serverUrl)

        if (uiState.errorMessages.isNotEmpty()) {

            val errorMessage = remember(uiState) { uiState.errorMessages[0] }
            val errorMessageText: String = stringResource(errorMessage.messageId)
            val retryMessageText = stringResource(id = R.string.retry)
            val onRefreshSmovDataState by rememberUpdatedState(onRefreshSmovData)
            val onErrorDismissState by rememberUpdatedState(onErrorDismiss)

            LaunchedEffect(errorMessageText, retryMessageText, scaffoldState) {
                val snackbarResult = snackbarHostState.showSnackbar(
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
}

@Composable
fun ChannelNameBar(
    channelName: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    onRefreshSmovData: () -> Unit = { },
    onOpenBarScann: () -> Unit = { },
    serverUrl: String
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
                    text = serverUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
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
        }
    )
}


@Composable
private fun NodataOperate(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick = onRefresh,
        modifier = modifier
            .padding(16.dp)
            .navigationBarsPadding()
            .offset(x = 0.dp, y = (-32).dp)
            .height(48.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
    ) { Text("重载") }
}

@Composable
private fun MainFloatingActionButton(
    unit: () -> Unit,
    modifier: Modifier = Modifier,
    text: String
) {
    ExtendedFloatingActionButton(
        onClick = unit,
        modifier = modifier
            .padding(16.dp)
            .navigationBarsPadding()
            .offset(x = 0.dp, y = (-32).dp)
            .height(48.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
    ) { Text(text) }
}

@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SmovList(
    smov: List<Smov>,
    serverUrl: String,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    openSmovDetail: (Long, String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    //statusBar 会出现高度突然刷新的情况
    val contentPadding =
        WindowInsets.statusBarsIgnoringVisibility.add(WindowInsets(top = 70.dp)).asPaddingValues()
    Box(modifier = modifier) {
        LazyColumn(
            reverseLayout = false,
            state = scrollState,
            contentPadding = contentPadding,
            modifier = Modifier
                .testTag("ConversationTestTag")
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            items(smov) { smovItem ->
                SmovCard(smovItem, serverUrl, openSmovDetail)
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
    SmovBookMTheme(isDarkTheme = false) {
        HomeScreen(
            onErrorDismiss = {},
            uiState = testDataHasData,
            homeListLazyListState = rememberLazyListState(0),
            scaffoldState = rememberScaffoldState(),
            serverUrl = "127.0.0.1:8080",
            loadingState = NetworkState.SUCCESS,
            fetchNextSmovPage = { },
            changeServerUrl = {},
            openSmovDetail = { _, _ -> }
        )
    }

}


@Preview
@Composable
fun ChannelBarPrev() {
    SmovBookMTheme {
        ChannelNameBar(
            channelName = "composers",
            serverUrl = "127.0.0.1:8080"
        )
    }
}

@Preview
@Composable
fun DayHeaderPrev() {
    DayHeader("Aug 6")
}


private val JumpToBottomThreshold = 56.dp


