package com.leri.smovbook.ui.barcodeScann

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leri.smovbook.R
import com.leri.smovbook.ui.theme.SmovBookMTheme
import com.leri.smovbook.ui.theme.scanBorder
import com.leri.smovbook.ui.theme.scanIco
import com.leri.smovbook.ui.theme.scanMask

@Composable
fun BarCodeScanTitle(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(0.dp,50.dp,0.dp,0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_qr_code_scanner_24),
            tint = scanIco,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .height(60.dp).width(60.dp),
            contentDescription = stringResource(id = R.string.search)
        )
        Text(text = stringResource(id = R.string.scan_server_qrcode), fontSize = 17.sp,fontWeight = FontWeight.W600, modifier = Modifier.padding(top = 20.dp))
    }

}

@Composable
fun BarCodeScanMock(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp))
            //.background(color = scanMask)
            .border(4.dp, color = scanBorder),
    ) {
        Divider(thickness = 40.dp, color = scanMask)

        Divider(
            modifier = Modifier
                .height((310).dp)
                .width(40.dp)
                .align(Alignment.BottomStart), color = scanMask
        )

        Divider(
            thickness = 40.dp,
            color = scanMask,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width((310).dp)
        )

        Divider(
            modifier = Modifier
                .height((270).dp)
                .width(40.dp)
                .align(Alignment.CenterEnd), color = scanMask
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
fun BarCodeScanMockmPreview() {
    SmovBookMTheme() {
        Surface {
            Column(
                Modifier
                    .width(350.dp)
                    .height(350.dp)) {
                BarCodeScanMock()
            }
        }
    }
}