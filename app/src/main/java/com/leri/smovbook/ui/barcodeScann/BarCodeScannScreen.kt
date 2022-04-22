package com.leri.smovbook.ui.barcodeScann

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.leri.smovbook.barcodeScann.CameraPreview
import com.leri.smovbook.util.DataStoreUtils

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun BarCodeScannScreen(
    changeServer: () -> Unit,
) {
    var code by remember {
        mutableStateOf("Camera Permission")
    }
    Surface(color = MaterialTheme.colors.background) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            val cameraPermissionState =
                rememberPermissionState(permission = Manifest.permission.CAMERA)
            Button(
                onClick = {
                    cameraPermissionState.launchPermissionRequest()
                }
            ) {
                Text(text = code)
            }
            Spacer(modifier = Modifier.height(10.dp))

            CameraPreview { result ->
                code = result
                DataStoreUtils.putData("server_url", result)
            }
        }
    }
}