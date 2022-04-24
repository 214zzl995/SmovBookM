package com.leri.smovbook.ui.home

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key

@Composable
fun HomeRoute(
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    homeViewModel: HomeViewModel,
    openBarScann: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsState()
    HomeRoute(
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
        uiState = uiState,
        onErrorDismiss = { homeViewModel.errorShown(it) },
        openBarScann = openBarScann
    )

}


@Composable
fun HomeRoute(
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onErrorDismiss: (Long) -> Unit,
    uiState: HomeUiState,
    openBarScann: () -> Unit
) {

    val homeListLazyListState = rememberLazyListState()
    HomeScreen(
        onErrorDismiss = onErrorDismiss,
        uiState = uiState,
        homeListLazyListState = homeListLazyListState,
        scaffoldState = scaffoldState,
        openDrawer = openDrawer,
        openBarScann = openBarScann
    )

}
