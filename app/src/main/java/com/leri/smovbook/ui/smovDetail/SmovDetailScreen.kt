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
    smov: Smov?,
    smovName: String,
    serverUrl: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }

    val contentPadding = WindowInsets.statusBarsIgnoringVisibility.asPaddingValues()

    //val url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
    //val url = "https://v-cdn.zjol.com.cn/276994.mp4"

    //设置要点 要点1.确定视频窗体在 加载过程无法操作 要点二.smov已经加载完成后 需要重新设置他的url
    //可能的解决方案1 当数据出现变更时 将方法给 viewmodel执行 在当前的compose 进行监听 可能将方法传递给viewmodel 更加优雅
    //可能的解决方案2 设置一个伪界面 添加加载的样式 让用户看不出来
    //反正当前的 取到smov再渲染虽有用 但是不好
    //突然想到我好sb 完美的结局方案时把 那些数据直接从主页传过来 其他数据照常获取就好了。。。
    val subTitle = "http://img.cdn.guoshuyu.cn/subtitle2.srt"
    val url =
        "http://$serverUrl/smovbook/file/${smov?.realname}/${smov?.realname}.${smov?.extension}"

    val videoView = rememberVideoPlayerState(title = "", url = url, subTitle)

    LaunchedEffect(key1 = url) {
        if (smov != null) {
            videoView.smovInit(url = url, title = smovName, subTitle = subTitle)
        }
    }

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
                    videoView
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
    )
}





