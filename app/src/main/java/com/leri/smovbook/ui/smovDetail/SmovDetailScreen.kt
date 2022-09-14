package com.leri.smovbook.ui.smovDetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.leri.smovbook.models.entities.Smov
import com.leri.smovbook.ui.data.testDataSin


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SmovDetailScreen(
    smov: Smov,
    smovName: String,
    smovId: Long,
    serverUrl: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }

    val contentPadding = WindowInsets.statusBarsIgnoringVisibility.asPaddingValues()

    val url = "http://$serverUrl/smovbook/file/IPX-215/IPX-215.mp4"

    println("测试$url")


    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets
                    .navigationBars
                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
            ),
        topBar = {
            SmovDetailAppBar(
                scrollBehavior = scrollBehavior,
                title = smovName,
                onBack = onBack,
                modifier = Modifier.padding(contentPadding)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {

        }

    }


}

@Preview
@Composable
fun SmovDetailScreenPreview() {
    SmovDetailScreen(smov = testDataSin, smovName = "SmovBook", onBack = { }, serverUrl = "", smovId = 10086)
}





