package com.leri.smovbook.ui.smovDetail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.leri.smovbook.models.entities.*
import com.leri.smovbook.viewModel.SmovDetailViewModel

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun SmovDetailRouter(
    smovId: Long,
    smovName: String,
    serverUrl: String,
    viewModel: SmovDetailViewModel,
    onBack: () -> Unit,
) {

    val smov by viewModel.smovFlow.collectAsState(initial = initialSmov)

    val pageState by viewModel.pageState

    LaunchedEffect(key1 = smovId) {
        viewModel.fetchSmovDetailsById(smovId)
    }

    SmovDetailScreen(
        smov = smov,
        serverUrl = serverUrl,
        smovName = smovName,
        onBack = onBack,
        pageState = pageState
    )
}

private val initialSmov = Smov(
    0,
    "",
    "",
    "",
    "",
    "",
    0,
    0,
    0,
    "",
    "",
    0,
    SmovAttr(0, ""),
    SmovAttr(0, ""),
    SmovAttr(0, ""),
    SmovAttr(0, ""),
    listOf(Tag(1, "")),
    listOf(Actor(1, "")),
    false,
    ImageFile(
        "",
        Size(1920, 1080)
    ),
    "",
    listOf(""),
)