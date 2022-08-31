package com.leri.smovbook.ui.barcodeScann

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leri.smovbook.ui.theme.ChangeServerUrlBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlSelect(
    barcodes: List<String>,
    changeServer: (String) -> Unit,
) {
    Surface(color = ChangeServerUrlBackground) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            contentAlignment = Alignment.Center
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(12.dp)
                ) {
                    for (barcode in barcodes) {
                        ElevatedButton(
                            onClick = { changeServer(barcode) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(barcode) }
                    }
                }
            }


        }
    }
}

@Preview
@Composable
fun UrlSelectPrev() {
    UrlSelect(listOf("127.0.0.1:8080", "127.0.0.1:8080"), changeServer = {})
}