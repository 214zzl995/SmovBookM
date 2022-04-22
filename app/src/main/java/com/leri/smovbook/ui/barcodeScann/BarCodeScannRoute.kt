package com.leri.smovbook.ui.barcodeScann

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BarCodeScannRoute(
    changeServer: () -> Unit,
) {
    BarCodeScannScreen {
        changeServer()
    }

}