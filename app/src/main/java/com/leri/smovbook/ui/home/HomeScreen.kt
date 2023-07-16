package com.leri.smovbook.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leri.smovbook.R
import com.leri.smovbook.ui.theme.SmovBookMTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import com.leri.smovbook.models.entities.Smov
import com.leri.smovbook.models.network.NetworkState
import com.leri.smovbook.models.network.isError
import com.leri.smovbook.models.network.isLoading
import com.leri.smovbook.models.network.isSuccess
import com.leri.smovbook.ui.components.*
import com.leri.smovbook.ui.data.testDataHasData

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
    pageState: NetworkState,
    fetchNextSmovPage: () -> Unit,
    openSmovDetail: (Long, String) -> Unit,
    serverState: ServerState,
) {

    val scrollState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var changeServerUrlDialogVisible by remember { mutableStateOf(false) }

    Surface(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                Modifier
                    .fillMaxSize()
            ) {
                HomeScreenWithList(
                    uiState = uiState,
                    onRefreshSmovData = onRefreshSmovData,
                    onErrorDismiss = onErrorDismiss,
                    openDrawer = openDrawer,
                    homeListLazyListState = homeListLazyListState,
                    openBarScann = { changeServerUrlDialogVisible = true },
                    scaffoldState = scaffoldState,
                    pageState = pageState,
                    serverState = serverState,
                    fetchNextSmovPage = fetchNextSmovPage,
                    scrollBehavior = scrollBehavior
                ) { hasData ->
                    SmovList(
                        smov = hasData.smovs,
                        serverUrl = serverState.serverUrl,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        scrollState = scrollState,
                        openSmovDetail = openSmovDetail,
                        scrollBehavior = scrollBehavior
                    )

                }
            }

        }
        ChangeServerUrlDialog(
            openBarScann,
            changeServerUrlDialogVisible,
            close = { changeServerUrlDialogVisible = false },
            changeServerUrl = serverState.changeServerUrl
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenWithList(
    uiState: HomeUiState,
    onErrorDismiss: (Long) -> Unit,
    onRefreshSmovData: () -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    openBarScann: () -> Unit,
    pageState: NetworkState,
    serverState: ServerState,
    fetchNextSmovPage: () -> Unit,
    hasPostsContent: @Composable (
        uiState: HomeUiState.HasData,
    ) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (uiState.errorMessages.isEmpty() && !pageState.isLoading() && uiState.errorMessages.count() > 100) {
                ExtendedFloatingActionButton(
                    onClick = onRefreshSmovData
                ) { Text("重载") }
            }
        },
    )
    { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

         RefreshContent(
             empty = when (uiState) {
                 is HomeUiState.HasData -> false
                 is HomeUiState.NoData -> pageState.isLoading()
             },
             emptyContent = { FullScreenLoading() },
             loading = pageState.isLoading(),
             onRefresh = onRefreshSmovData,
             content = {
                 Crossfade(targetState = uiState, label = "") {
                     when (it) {
                         is HomeUiState.HasData -> hasPostsContent(it)
                         is HomeUiState.NoData -> {
                             Box(contentModifier.fillMaxSize()) {
                                 if (pageState.isError()) {
                                     WrongRequest()
                                 } else if (pageState.isSuccess()) {
                                     EmptyData()
                                 }
                                 NodataOperate(
                                     onRefresh = onRefreshSmovData,
                                     modifier = Modifier.align(Alignment.BottomEnd)
                                 )
                             }

                         }
                     }
                 }
             },
         )

        val name by remember { mutableStateOf("SmovBook") }

        ChannelNameBar(
            channelName = name,
            onNavIconPressed = openDrawer,
            scrollBehavior = scrollBehavior,
            modifier = Modifier,
            onRefreshSmovData = onRefreshSmovData,
            onOpenBarScann = openBarScann,
            serverState = serverState,
        )

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
private fun NodataOperate(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
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
    text: String,
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
private fun RefreshContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit,
) {
    Crossfade(targetState = empty, label = "") { targetState ->
        if (targetState) {
            emptyContent()
        } else {
            Box(Modifier) {
                content()
            }
        }

    }

}


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SmovList(
    smov: List<Smov>,
    serverUrl: String,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    openSmovDetail: (Long, String) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {

    val scope = rememberCoroutineScope()
    //statusBar 会出现高度突然刷新的情况
    val contentPadding =
        WindowInsets.statusBarsIgnoringVisibility.add(WindowInsets(top = 70.dp)).asPaddingValues()

    BoxWithConstraints(modifier = modifier) {

        LazyColumn(
            reverseLayout = false,
            state = scrollState,
            contentPadding = contentPadding,
            modifier = Modifier
                .testTag("ConversationTestTag")
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize()
        ) {
            items(smov) { smovItem ->
                SmovCard(smovItem, serverUrl, openSmovDetail)
            }
        }

        //这个部分的实现需要很多一起 所以先改造LazyColumn部分
        /*Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.15f)
                .background(Color.Red).align(Alignment.BottomEnd),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(text = "现在不知道咋整 等下看")

        }*/


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
            serverState = ServerState("127.0.0.1:8000", mutableListOf()),
            pageState = NetworkState.SUCCESS,
            fetchNextSmovPage = { },
            openSmovDetail = { _, _ -> }
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ChannelBarPrev() {
    SmovBookMTheme {
        ChannelNameBar(
            channelName = "composers",
            serverState = ServerState("127.0.0.1:8000", mutableListOf()),
        )
    }
}

@Preview
@Composable
fun DayHeaderPrev() {
    DayHeader("Aug 6")
}


private val JumpToBottomThreshold = 56.dp


