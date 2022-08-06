package com.leri.smovbook.ui.barcodeScann

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun BarCodeScannScreen(
    navigateUp: () -> Unit,
    changeServer: (String) -> Unit,
) {

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.Start,
        ) {

            CameraPreview(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp))
                    .width(300.dp)
                    .height(300.dp)
            ) { result ->
                changeServer(result)
                navigateUp()
            }
        }
    }
}

