package com.leri.smovbook.ui.serviceSwitch

import android.Manifest
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.leri.smovbook.ui.AppNavigationActions
import com.leri.smovbook.ui.home.HomeViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ServiceSwitchRoute(
    homeViewModel: HomeViewModel,
    navigationActions: AppNavigationActions,
) {
    val serverState by homeViewModel.serverState.collectAsState()

    val cameraPermissionState =
        rememberPermissionState(
            permission = Manifest.permission.CAMERA,
            onPermissionResult = {
                if (it) {
                    navigationActions.navigateToBarCode()
                } else {
                    return@rememberPermissionState
                }
            })
    ServiceSwitchScreen(
        serverState = serverState,
        openBarScann = {
            when (cameraPermissionState.status) {
                PermissionStatus.Granted -> {
                    navigationActions.navigateToBarCode()
                }

                is PermissionStatus.Denied -> {
                    cameraPermissionState.launchPermissionRequest()
                }
            }
        },
    )

}