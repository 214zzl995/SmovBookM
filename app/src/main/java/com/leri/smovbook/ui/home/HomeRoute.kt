package com.leri.smovbook.ui.home

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@Composable
fun HomeRoute(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    homeViewModel: HomeViewModel,
    openBarScann: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsState()
    HomeRoute(
        scaffoldState = scaffoldState,
        uiState = uiState,
        onErrorDismiss = { homeViewModel.errorShown(it) },
        openBarScann = openBarScann,
        onRefreshSmovData = { homeViewModel.refreshData() }
    )

}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeRoute(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onErrorDismiss: (Long) -> Unit,
    uiState: HomeUiState,
    openBarScann: () -> Unit,
    onRefreshSmovData: () -> Unit
) {

    val context = LocalContext.current

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

    val homeScreenType = getHomeScreenType(uiState)

    BackHandler(
        onBack = {
            coroutineScope.launch {
                val nowTime = System.currentTimeMillis();
                if (nowTime - backTime > 2000) {
                    Toast.makeText(context, "再次返回退出程序", Toast.LENGTH_SHORT).show()
                    backTime = nowTime
                } else {
                    exitProcess(1)
                }
            }
        },
        enabled = homeScreenType == HomeScreenType.Feed
    )

    val homeListLazyListState = rememberLazyListState()
    HomeScreen(
        modifier = Modifier.windowInsetsPadding(
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
        onRefreshSmovData = onRefreshSmovData
    )

}

private enum class HomeScreenType {
    Feed,
    Details
}

@Composable
private fun getHomeScreenType(
    uiState: HomeUiState
): HomeScreenType =
    when (uiState) {
        is HomeUiState.HasData -> {
            if (uiState.isDetailOpen) {
                HomeScreenType.Details
            } else {
                HomeScreenType.Feed
            }
        }
        is HomeUiState.NoData -> HomeScreenType.Feed
    }
