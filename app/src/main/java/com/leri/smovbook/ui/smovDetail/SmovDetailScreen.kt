package com.leri.smovbook.ui.smovDetail

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.leri.smovbook.models.entities.Smov
import com.leri.smovbook.ui.data.testDataSin
import com.leri.smovbook.ui.player.SmovVideoState
import com.leri.smovbook.ui.player.SmovVideoView


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

    //val url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
    val subTitle = "http://img.cdn.guoshuyu.cn/subtitle2.srt"
    val url = "http://192.168.31.8:8000/smovbook/file/PPPD-927/PPPD-927.mp4"

    val videoView = rememberVideoPlayerState(title = "TEST", url = url, subTitle)

    BackHandler {
        if (videoView.isIfCurrentIsFullscreen) {
            videoView.onBackFullscreen()
        } else {
            onBack()
            videoView.release()
        }
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            AndroidView(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .background(Color.White),
                factory = {
                    /**
                     * 对于全屏的问题的猜想
                     * 其实全屏没有问题 但是设置当前方向出现了问题 就是当我点击了按钮之后 去设置方向出现了问题
                     */
                    videoView.apply {
                        smovInit()
                    }
                })
        }

    }
}

@Composable
fun rememberVideoPlayerState(
    title: String,
    url: String,
    subTitle: String?
): SmovVideoView {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    return rememberSaveable(
        context, coroutineScope,
        saver = object : Saver<SmovVideoView, SmovVideoState> {
            override fun restore(value: SmovVideoState): SmovVideoView {
                return SmovVideoView(
                    context = context,
                    title = value.title,
                    url = value.url,
                    subTitle = value.subTitle
                )
            }

            override fun SaverScope.save(value: SmovVideoView): SmovVideoState {
                return SmovVideoState(
                    isIfCurrentIsFullscreen = value.isIfCurrentIsFullscreen,
                    title = value.title,
                    url = value.url,
                    subTitle = value.subTitle
                )
            }
        },
        init = {
            SmovVideoView(
                context = context,
                title = title,
                url = url,
                subTitle = subTitle
            )
        }
    )
}


fun Context.getActivity(): AppCompatActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is AppCompatActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
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





