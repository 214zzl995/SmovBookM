package com.leri.smovbook.ui.smovDetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import cn.jzvd.JZMediaSystem
import cn.jzvd.JzvdStd
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
            //证明方案可行 需要模仿AgVideo 用jetpack compose 写一个播放器
            //文档地址 http://jzvd.org/jzplayer/extends-usage.html
            //项目地址 https://github.com/Jzvd/JZVideo/tree/develop/demo
            AndroidView(factory = {
                JzvdStd(it).apply {
                    setUp(
                        url,
                        "饺子闭眼睛",
                        JzvdStd.SCREEN_NORMAL
                    )
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





