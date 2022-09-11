package com.leri.smovbook.ui.smovDetail

import androidx.compose.runtime.Composable
import com.leri.smovbook.ui.data.testDataSin


@Composable
fun SmovDetailRouter(
    smovId: Long,
    smovName: String,
    viewModel: SmovDetailViewModel,
    onBack: () -> Unit,
) {

    SmovDetailScreen(smov = testDataSin, smovName = smovName, onBack = onBack)
}