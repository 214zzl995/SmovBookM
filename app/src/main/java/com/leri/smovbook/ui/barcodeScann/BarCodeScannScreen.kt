package com.leri.smovbook.ui.barcodeScann

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

@Composable
fun BarCodeScannScreen(
    navigateUp: () -> Unit,
    changeServer: (String) -> Unit,
) {

    Surface() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CameraPreview { result ->
                changeServer(result)
                navigateUp()
            }
        }
    }
}