package com.leri.smovbook.ui.smovDetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import cn.jzvd.JzvdStd
import com.leri.smovbook.models.entities.Smov
import com.leri.smovbook.ui.data.testDataSin
import com.leri.smovbook.ui.player.JzvdStdShowTitleAfterFullscreen
import timber.log.Timber


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

    val url = "http://192.168.31.8:8000/smovbook/file/PPPD-927/PPPD-927.mp4"

    //val url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"


    var fullscreen by rememberSaveable { mutableStateOf(false) }

    /* 需要自定义Saver 参考那个看吐了的项目 不然旋转会出现问题
    val videoPlayer = rememberSaveable {
         JzvdStdShowTitleAfterFullscreen(context).apply {
             setUp(
                 url,
                 "测试",
                 JzvdStd.SCREEN_NORMAL
             )
             changeFullscreen = { fullscreen = !fullscreen }
         }
     }*/

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
            if (!fullscreen) {
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
            //证明方案可行 需要模仿AgVideo 用jetpack compose 写一个播放器
            //文档地址 http://jzvd.org/jzplayer/extends-usage.html
            //项目地址 https://github.com/Jzvd/JZVideo/tree/develop/demo
            AndroidView(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(if (fullscreen) 1f else 0.3f), factory = {
                JzvdStdShowTitleAfterFullscreen(context).apply {
                    setUp(
                        url,
                        "测试",
                        JzvdStd.SCREEN_NORMAL
                    )
                    changeFullscreen = { fullscreen = !fullscreen }
                }
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





