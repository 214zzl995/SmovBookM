package com.leri.smovbook.ui.barcodeScann

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.leri.smovbook.R
import com.leri.smovbook.ui.theme.*

@Composable
fun BarCodeScanTitle(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(0.dp, 50.dp, 0.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.qr_code))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            LottieAnimation(composition = composition, progress = { progress })
        }
    }

}

@Composable
fun BarCodeScanMock(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp))
            //.background(color = scanMask)
            .border(4.dp, color = MaterialTheme.colorScheme.onSurface),
    ) {
        Divider(thickness = 40.dp, color = MaterialTheme.colorScheme.onSurface)

        Divider(
            modifier = Modifier
                .height((310).dp)
                .width(40.dp)
                .align(Alignment.BottomStart), color = MaterialTheme.colorScheme.onSurface
        )

        Divider(
            thickness = 40.dp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width((310).dp)
        )

        Divider(
            modifier = Modifier
                .height((270).dp)
                .width(40.dp)
                .align(Alignment.CenterEnd), color = MaterialTheme.colorScheme.onSurface
        )
    }

}


@Preview
@Composable
fun BarCodeScanTitlePreview() {
    SmovBookMTheme() {
        Surface {
            Column {
                BarCodeScanTitle()
            }
        }
    }
}

@Preview
@Composable
fun BarCodeScanMockPreview() {
    SmovBookMTheme() {
        Surface {
            Column(
                Modifier
                    .width(350.dp)
                    .height(350.dp)
            ) {
                BarCodeScanMock()
            }
        }
    }
}