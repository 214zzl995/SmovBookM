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
import com.leri.smovbook.barcodeScann.CameraPreview

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