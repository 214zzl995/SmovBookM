package com.leri.smovbook.ui.smovDetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.leri.smovbook.models.entities.Smov
import com.leri.smovbook.ui.data.testDataSin
import com.leri.videoplayer_media3.VideoPlayer
import com.leri.videoplayer_media3.VideoPlayerSource
import com.leri.videoplayer_media3.rememberVideoPlayerController


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

    val videoPlayerController = rememberVideoPlayerController()

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(videoPlayerController, lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                videoPlayerController.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(key1 = smovId) {
        videoPlayerController.setSource(VideoPlayerSource.Network("https://prod-streaming-video-msn-com.akamaized.net/ba258271-89c7-47bc-9742-bcae67c23202/f7ff4fe4-1346-47bb-9466-3f4662c1ac3a.mp4"))
    }

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
        println(innerPadding)
        //参考https://exoplayer.dev/
        //全屏参考 https://stackoverflow.com/questions/72102097/jetpack-compose-exoplayer-full-screen
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            VideoPlayer(
                modifier = Modifier,
                videoPlayerController = videoPlayerController
            )
        }

    }


}

@Preview
@Composable
fun SmovDetailScreenPreview() {
    SmovDetailScreen(smov = testDataSin, smovName = "SmovBook", onBack = { }, serverUrl = "", smovId = 10086)
}





