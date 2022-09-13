package com.leri.smovbook.ui.smovDetail

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.leri.smovbook.models.entities.Smov
import com.leri.smovbook.ui.data.testDataSin
import com.leri.smovbook.ui.videoplayer.VideoPlayer
import com.leri.smovbook.ui.videoplayer.VideoPlayerSource
import com.leri.smovbook.ui.videoplayer.rememberVideoPlayerController


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SmovDetailScreen(
    smov: Smov,
    smovName: String,
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

    LaunchedEffect(smovName) {
        videoPlayerController.setSource(VideoPlayerSource.Network("http://127.0.0.1/smovbook/file/${smov.realname}/${smov.realname}.${smov.extension}"))
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
    SmovDetailScreen(smov = testDataSin, smovName = "SmovBook", onBack = { })
}





