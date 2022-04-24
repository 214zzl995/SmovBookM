package com.leri.smovbook.ui.barcodeScann

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BarCodeScannRoute(
    navigateUp: () -> Unit,
    changeServer: (String) -> Unit
) {
    BarCodeScannScreen(navigateUp = navigateUp, changeServer = {
        changeServer(it)
    })

}