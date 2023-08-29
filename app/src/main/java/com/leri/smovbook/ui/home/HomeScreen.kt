package com.leri.smovbook.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.rememberScaffoldState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import com.leri.smovbook.models.entities.Smov
import com.leri.smovbook.models.network.NetworkState
import com.leri.smovbook.models.network.isError
import com.leri.smovbook.models.network.isLoading
import com.leri.smovbook.models.network.isSuccess
import com.leri.smovbook.ui.components.*
import com.leri.smovbook.ui.data.testDataHasData
import com.leri.smovbook.viewModel.HomeUiState
import com.leri.smovbook.viewModel.SettingsViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onErrorDismiss: (Long) -> Unit,
    uiState: HomeUiState,
    onRefreshSmovData: () -> Unit = { },
    scaffoldState: ScaffoldState,
    pageState: NetworkState,
    fetchNextSmovPage: () -> Unit,
    openSmovDetail: (Long, String) -> Unit,
    serviceUrl: String,
) {

    val scrollState = rememberLazyListState()

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
                    scaffoldState = scaffoldState,
                    pageState = pageState,
                    serviceUrl = serviceUrl,
                    fetchNextSmovPage = fetchNextSmovPage,
                ) { hasData ->
                    SmovList(
                        smov = hasData.smovs,
                        serviceUrl = serviceUrl,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        scrollState = scrollState,
                        openSmovDetail = openSmovDetail
                    )

                }
            }

        }

    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenWithList(
    uiState: HomeUiState,
    onErrorDismiss: (Long) -> Unit,
    onRefreshSmovData: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    pageState: NetworkState,
    serviceUrl: String,
    fetchNextSmovPage: () -> Unit,
    hasPostsContent: @Composable (
        uiState: HomeUiState.HasData,
    ) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "首页",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    Row(modifier = Modifier.padding(start = 10.dp)) {
                        Icon(
                            modifier = Modifier.height(24.dp),
                            imageVector = Icons.Outlined.Home,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = stringResource(id = R.string.info)
                        )
                    }

                },
                actions = {
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
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
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

        //这个部分很乱 接下来的改造重点
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
                        is HomeUiState.HasData -> Box(contentModifier.fillMaxSize()) {
                            hasPostsContent(
                                it
                            )
                        }

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

        if (uiState.errorMessages.isNotEmpty()) {

            val errorMessage = remember(uiState) { uiState.errorMessages[0] }
            val errorMessageText: String = stringResource(errorMessage.messageId)
            val retryMessageText = stringResource(id = R.string.retry)
            val onRefreshSmovDataState by rememberUpdatedState(onRefreshSmovData)
            val onErrorDismissState by rememberUpdatedState(onErrorDismiss)

            LaunchedEffect(errorMessageText, retryMessageText, scaffoldState) {
                val snackbarResult = snackBarHostState.showSnackbar(
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


@Composable
fun SmovList(
    smov: List<Smov>,
    serviceUrl: String,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    openSmovDetail: (Long, String) -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {

    val scope = rememberCoroutineScope()
    val thirdPartyPlayer by settingsViewModel.thirdPartyPlayer

    BoxWithConstraints(modifier = modifier) {

        LazyColumn(
            reverseLayout = false,
            state = scrollState,
            modifier = Modifier
                .testTag("ConversationTestTag")
                .fillMaxSize()
        ) {
            items(smov) { smovItem ->
                SmovCard(smovItem, serviceUrl, openSmovDetail, thirdPartyPlayer)
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
            scaffoldState = rememberScaffoldState(),
            serviceUrl = "127.0.0.1",
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
            serviceUrl = "127.0.0.1"
        )
    }
}

@Preview
@Composable
fun DayHeaderPrev() {
    DayHeader("Aug 6")
}


private val JumpToBottomThreshold = 56.dp


