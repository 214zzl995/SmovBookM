package com.leri.smovbook.ui.home

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.leri.smovbook.models.network.NetworkState
import com.leri.smovbook.ui.components.AppScaffold
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    homeViewModel: HomeViewModel,
    openBarScann: () -> Unit,
    currentRoute: String,
    openSmovDetail: (Long, String) -> Unit,
) {
    val uiState by homeViewModel.smovsState
    val detailOpen by homeViewModel.detailOpen
    val pageState by homeViewModel.pageState
    val serverState by homeViewModel.serverState

    HomeRoute(
        scaffoldState = scaffoldState,
        uiState = uiState.toUiState(),
        onErrorDismiss = { homeViewModel.errorShown(it) },
        openBarScann = openBarScann,
        onRefreshSmovData = { homeViewModel.refreshData() },
        currentRoute = currentRoute,
        detailOpen = detailOpen,
        pageState = pageState,
        fetchNextSmovPage = { homeViewModel.fetchNextSmovPage() },
        openSmovDetail = openSmovDetail,
        serverState = serverState
    )

}


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onErrorDismiss: (Long) -> Unit,
    uiState: HomeUiState,
    openBarScann: () -> Unit,
    onRefreshSmovData: () -> Unit,
    currentRoute: String,
    detailOpen: DrawerValue,
    pageState: NetworkState,
    fetchNextSmovPage: () -> Unit,
    openSmovDetail: (Long, String) -> Unit,
    serverState: ServerState,
) {

    val context = LocalContext.current

    val drawerState = rememberDrawerState(initialValue = detailOpen)

    var backTime by remember { mutableStateOf(System.currentTimeMillis() - 20000) }
    val coroutineScope = rememberCoroutineScope()


    val cameraPermissionState =
        rememberPermissionState(
            permission = Manifest.permission.CAMERA,
            onPermissionResult = {
                if (it) {
                    openBarScann()
                } else {
                    return@rememberPermissionState
                }
            })

    val homeScreenType = getHomeScreenType(uiState, detailOpen)

    BackHandler(
        onBack = {
            coroutineScope.launch {
                drawerState.close()
            }
        },
        enabled = drawerState.isOpen
    )

    BackHandler(
        onBack = {
            coroutineScope.launch {
                val nowTime = System.currentTimeMillis()
                if (nowTime - backTime > 2000) {
                    Toast.makeText(context, "再次返回退出程序", Toast.LENGTH_SHORT).show()
                    backTime = nowTime
                } else {
                    exitProcess(1)
                }
            }
        },
        enabled = homeScreenType == HomeScreenType.Feed && drawerState.isClosed
    )

    val homeListLazyListState = rememberLazyListState()
    AppScaffold(
        drawerState = drawerState,
        currentRoute = currentRoute,
        closeDrawer = { coroutineScope.launch { drawerState.close() } },
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        serverState = serverState
    ) {
        HomeScreen(
            modifier = Modifier
                .windowInsetsPadding(
                    WindowInsets
                        .navigationBars
                        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                ),
            onErrorDismiss = onErrorDismiss,
            uiState = uiState,
            homeListLazyListState = homeListLazyListState,
            scaffoldState = scaffoldState,
            openBarScann = {
                when (cameraPermissionState.status) {
                    PermissionStatus.Granted -> {
                        openBarScann()
                    }

                    is PermissionStatus.Denied -> {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }
            },
            onRefreshSmovData = onRefreshSmovData,
            openDrawer = { coroutineScope.launch { drawerState.open() } },
            pageState = pageState,
            fetchNextSmovPage = fetchNextSmovPage,
            openSmovDetail = openSmovDetail,
            serverState  = serverState
        )
    }

}

private enum class HomeScreenType {
    Feed,
    Details
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun getHomeScreenType(
    uiState: HomeUiState,
    detailOpen: DrawerValue,
): HomeScreenType =
    when (uiState) {
        is HomeUiState.HasData -> {
            if (detailOpen.equals(DrawerValue.Open)) {
                HomeScreenType.Details
            } else {
                HomeScreenType.Feed
            }
        }

        is HomeUiState.NoData -> HomeScreenType.Feed
    }
