package com.leri.smovbook.ui.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.leri.smovbook.models.network.NetworkState
import com.leri.smovbook.ui.AppNavigationActions
import com.leri.smovbook.viewModel.HomeUiState
import com.leri.smovbook.viewModel.HomeViewModel
import com.leri.smovbook.viewModel.ServiceViewModel
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    homeViewModel: HomeViewModel,
    serviceViewModel: ServiceViewModel,
    navigationActions: AppNavigationActions,
    openSmovDetail: (Long, String) -> Unit,
) {
    val uiState by homeViewModel.smovsState
    val detailOpen by homeViewModel.detailOpen
    val pageState by homeViewModel.pageState
    val serviceUrl by serviceViewModel.serverUrl

    HomeRoute(
        scaffoldState = scaffoldState,
        uiState = uiState.toUiState(),
        onErrorDismiss = { homeViewModel.errorShown(it) },
        navigationActions = navigationActions,
        onRefreshSmovData = { homeViewModel.refreshData() },
        onDetailOpen = detailOpen,
        pageState = pageState,
        fetchNextSmovPage = { homeViewModel.fetchNextSmovPage() },
        openSmovDetail = openSmovDetail,
        serviceUrl = serviceUrl,
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onErrorDismiss: (Long) -> Unit,
    uiState: HomeUiState,
    navigationActions: AppNavigationActions,
    onRefreshSmovData: () -> Unit,
    onDetailOpen: DrawerValue,
    pageState: NetworkState,
    fetchNextSmovPage: () -> Unit,
    openSmovDetail: (Long, String) -> Unit,
    serviceUrl: String,
) {

    val context = LocalContext.current

    val drawerState = rememberDrawerState(initialValue = onDetailOpen)

    var backTime by remember { mutableLongStateOf(System.currentTimeMillis() - 20000) }
    val coroutineScope = rememberCoroutineScope()


    val homeScreenType = getHomeScreenType(uiState, onDetailOpen)

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

    return HomeScreen(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets
                    .navigationBars
                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
            ),
        onErrorDismiss = onErrorDismiss,
        uiState = uiState,
        scaffoldState = scaffoldState,
        onRefreshSmovData = onRefreshSmovData,
        pageState = pageState,
        fetchNextSmovPage = fetchNextSmovPage,
        openSmovDetail = openSmovDetail,
        serviceUrl = serviceUrl,
    )

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
