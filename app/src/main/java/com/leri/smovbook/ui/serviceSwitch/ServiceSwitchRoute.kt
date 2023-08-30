package com.leri.smovbook.ui.serviceSwitch

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.leri.smovbook.ui.AppNavigationActions
import com.leri.smovbook.viewModel.ServiceViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ServiceSwitchRoute(
    serviceViewModel: ServiceViewModel,
    navigationActions: AppNavigationActions,
) {
    val serviceUrl by serviceViewModel.serverUrl
    val historyUrl by serviceViewModel.historyUrl

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
        serviceUrl = serviceUrl,
        historyUrl = historyUrl,
        addServiceUrl = { serviceViewModel.addServiceUrl(it) },
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
        removeServiceUrl = { serviceViewModel.removeServiceUrl(it) },
        changeServiceUrl = { index, url -> serviceViewModel.changeServiceUrl(index, url) },

    )

}