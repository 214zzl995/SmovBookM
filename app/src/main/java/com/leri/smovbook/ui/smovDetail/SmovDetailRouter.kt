package com.leri.smovbook.ui.smovDetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.leri.smovbook.ui.data.testDataSin


@Composable
fun SmovDetailRouter(
    smovId: Long,
    smovName: String,
    serverUrl: String,
    viewModel: SmovDetailViewModel,
    onBack: () -> Unit,
) {

    val smov by viewModel.smovFlow.collectAsState(initial = null)

    LaunchedEffect(key1 = smovId) {
        viewModel.fetchSmovDetailsById(smovId)
    }

    SmovDetailScreen(
        smov = smov,
        serverUrl = serverUrl,
        smovName = smovName,
        onBack = onBack
    )
}