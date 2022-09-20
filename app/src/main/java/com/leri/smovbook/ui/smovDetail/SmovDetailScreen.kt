package com.leri.smovbook.ui.smovDetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import cn.jzvd.Jzvd
import com.leri.smovbook.models.entities.Smov
import com.leri.smovbook.ui.data.testDataSin
import com.leri.smovbook.ui.player.rememberVideoPlayer


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

    val context = LocalContext.current

    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }

    val contentPadding = WindowInsets.statusBarsIgnoringVisibility.asPaddingValues()

    val url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"

    val videoPlayer = rememberVideoPlayer(url, smovName)

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
            if (videoPlayer.screen != Jzvd.SCREEN_FULLSCREEN) {
                SmovDetailAppBar(
                    scrollBehavior = scrollBehavior,
                    title = smovName,
                    onBack = onBack,
                    modifier = Modifier.padding(contentPadding)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            AndroidView(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(if (videoPlayer.screen == Jzvd.SCREEN_FULLSCREEN) 1f else 0.3f), factory = {
                videoPlayer


            })


        }

    }
}


@Preview
@Composable
fun SmovDetailScreenPreview() {
    SmovDetailScreen(
        smov = testDataSin,
        smovName = "SmovBook",
        onBack = { },
        serverUrl = "",
        smovId = 10086
    )
}





