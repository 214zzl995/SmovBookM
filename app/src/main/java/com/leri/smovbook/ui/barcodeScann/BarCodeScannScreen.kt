package com.leri.smovbook.ui.barcodeScann

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@Composable
fun BarCodeScannScreen(
    navigateUp: () -> Unit,
    changeServer: (String) -> Unit,
) {

    var scanEnable by remember { mutableStateOf(true) }
    var urlSelectVisible by remember { mutableStateOf(false) }
    var barcodes by remember { mutableStateOf(listOf<String>()) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxHeight(0.7f)

            ) {
                CameraPreview(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp))
                        .shadow(6.dp),
                    scanEnable = scanEnable,
                    disableScan = { scanEnable = false }
                ) { result ->
                    if (result.size == 1) {
                        changeServer(result[0].replace("http://", ""))
                        navigateUp()
                    } else {
                        scanEnable = false
                        barcodes = result
                        urlSelectVisible = true
                    }
                }
            }
            BarCodeScanTitle()
        }
    }
    AnimatedVisibility(
        visible = urlSelectVisible,
        enter = fadeIn(), exit = fadeOut()
    ) {
        UrlSelect(barcodes = barcodes, changeServer = {
            changeServer(it.replace("http://", ""))
            navigateUp()
        })
    }
}

