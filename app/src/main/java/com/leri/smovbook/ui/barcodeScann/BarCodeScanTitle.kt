package com.leri.smovbook.ui.barcodeScann

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.leri.smovbook.ui.theme.SmovBookMTheme

@Composable
fun BarCodeScanMask() {
}

@Preview
@Composable
fun SmovItemPreview() {
    SmovBookMTheme() {
        Surface {
            Column {
                BarCodeScanMask()
            }
        }
    }
}