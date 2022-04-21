package com.leri.smovbook.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.gson.Gson
import com.leri.smovbook.R
import com.leri.smovbook.model.Smov
import com.leri.smovbook.ui.FunctionalityNotAvailablePopup
import com.leri.smovbook.ui.components.AppBar
import com.leri.smovbook.ui.theme.SmovBookMTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

/**
 * Entry point for a conversation screen.
 *
 * @param navigate User action when navigation to a profile is requested
 * @param modifier [Modifier] to apply to this layout node
 * @param onNavIconPressed Sends an event up when the user clicks on the menu
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmovContent(
    navigate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = { },
    viewModel: SmovContentViewModel
) {

    val scrollState = rememberLazyListState()
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }

    val client = OkHttpClient()

    val builder = Request.Builder()
    builder.url("http://192.168.88.67:8325/data")
    builder.addHeader("Content-Type", "application/json").get()

    var smovList by remember { mutableStateOf(listOf<Smov>()) }
    val gson = Gson()

    val onRefreshSmovData = {
        client.newCall(builder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body!!.string()
                smovList = gson.fromJson(res, Array<Smov>::class.java).asList()
            }
        })
    }
    onRefreshSmovData()

    Surface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                SmovList(
                    smovList,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    scrollState = scrollState,
                    onRefreshSmovData = onRefreshSmovData
                )
            }
            // Channel name bar floats above the messages
            ChannelNameBar(
                channelName = "SmovBook",
                onNavIconPressed = onNavIconPressed,
                scrollBehavior = scrollBehavior,
                // Use statusBarsPadding() to move the app bar content below the status bar
                modifier = Modifier.statusBarsPadding(),
                onRefreshSmovData = onRefreshSmovData,
                navigate = navigate,
                viewModel = viewModel
            )

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
    navigate: (Int) -> Unit = { },
    viewModel: SmovContentViewModel
) {
    var functionalityNotAvailablePopupShown by remember { mutableStateOf(false) }
    if (functionalityNotAvailablePopupShown) {
        FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
    }

    AppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        onNavIconPressed = onNavIconPressed,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Channel name
                Text(
                    text = channelName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = viewModel.getServer(), //stringResource(R.string.members, channelMembers)
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            // Search icon
            Icon(
                imageVector = Icons.Outlined.Search,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = { /*navigate(R.id.nav_bar_code) */ }) //functionalityNotAvailablePopupShown = true
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = stringResource(id = R.string.search)
            )
            // Info icon
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


class SwipeRefreshModel : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)

    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    fun refresh(onRefreshSmovData: () -> Unit = { }) {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            //delay(2000)
            onRefreshSmovData()
            _isRefreshing.emit(false)
        }
    }
}

@Composable
fun SmovList(
    smovList: List<Smov>,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    onRefreshSmovData: () -> Unit = { }
) {
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {

        val viewModel: SwipeRefreshModel = viewModel()
        val isRefreshing by viewModel.isRefreshing.collectAsState()
        //这个东西会占用布局！！
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { viewModel.refresh(onRefreshSmovData) },
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    scale = true,
                    modifier = Modifier.offset(y = 90.dp)
                )
            }
        ) {
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
                        for (smov in smovList) {
                            SmovItem(smov)
                        }
                    }
                }
            }
        }
        // Jump to bottom button shows up when user scrolls past a threshold.
        // Convert to pixels:
        val jumpThreshold = with(LocalDensity.current) {
            JumpToBottomThreshold.toPx()
        }

        // Show the button if the first visible item is not the first one or if the offset is
        // greater than the threshold.
        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        JumpToTop(
            // Only show if the scroller is not at the bottom
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
fun ChannelBarPrev() {
    SmovBookMTheme {
        ChannelNameBar(
            channelName = "composers",
            viewModel = SmovContentViewModel()
        )
    }
}

@Preview
@Composable
fun DayHeaderPrev() {
    DayHeader("Aug 6")
}

private val JumpToBottomThreshold = 56.dp


