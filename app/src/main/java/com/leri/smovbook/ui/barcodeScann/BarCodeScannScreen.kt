package com.leri.smovbook.ui.barcodeScann

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.leri.smovbook.ui.theme.scanBorder
import com.leri.smovbook.ui.theme.scanMask
import kotlinx.coroutines.runBlocking

@Composable
fun BarCodeScannScreen(
    navigateUp: () -> Unit,
    changeServer: (String) -> Unit,
) {

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    //.width(350.dp)
                    //.height(350.dp)
                    .fillMaxHeight(0.7f)

            ) {
                CameraPreview(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp)).shadow(6.dp)


                ) { result ->
                    changeServer(result)
                    navigateUp()
                }

                //BarCodeScanMock()

            }
            BarCodeScanTitle()


        }
    }
}

