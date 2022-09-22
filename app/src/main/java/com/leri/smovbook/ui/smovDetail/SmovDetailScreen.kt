package com.leri.smovbook.ui.smovDetail

import android.content.Context
import android.content.ContextWrapper
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.exoplayer2.SeekParameters
import com.leri.smovbook.models.entities.Smov
import com.leri.smovbook.ui.data.testDataSin
import com.leri.smovbook.ui.player.exosubtitle.GSYExoSubTitleVideoView
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager


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

    var isFullScreen by rememberSaveable { mutableStateOf(false) }

    val gsyVideoOption = GSYVideoOptionBuilder()

    val url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets
                    .navigationBars
                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
            ),
        topBar = {
            if (!isFullScreen) {
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
                .fillMaxHeight(if (isFullScreen) 1f else 0.3f),
                factory = {

                    GSYExoSubTitleVideoView(it).apply {
                        //外部辅助的旋转，帮助全屏
                        val orientationUtils = OrientationUtils(it.getActivity(), this)
                        //初始化不打开外部的旋转
                        orientationUtils.isEnable = false

                        gsyVideoOption
                            .setIsTouchWiget(true)
                            .setRotateViewAuto(true)
                            .setLockLand(false)
                            .setAutoFullWithSize(false)
                            .setShowFullAnimation(true)
                            .setNeedLockFull(true)
                            .setUrl(url)
                            .setCacheWithPlay(true)
                            .setVideoTitle("测试视频")
                            .setVideoAllCallBack(object : GSYSampleCallBack() {
                                override fun onPrepared(url: String, vararg objects: Any) {
                                    Debuger.printfError("***** onPrepared **** " + objects[0])
                                    Debuger.printfError("***** onPrepared **** " + objects[1])
                                    super.onPrepared(url, *objects)

                                    orientationUtils.isEnable = isRotateWithSystem
                                    //外部辅助的旋转，帮助全屏

                                    if (gsyVideoManager.player is Exo2PlayerManager
                                    ) {
                                        (gsyVideoManager.player as Exo2PlayerManager).setSeekParameter(
                                            SeekParameters.NEXT_SYNC
                                        )
                                        Debuger.printfError("***** setSeekParameter **** ")
                                    }
                                }

                                override fun onEnterFullscreen(url: String, vararg objects: Any) {
                                    super.onEnterFullscreen(url, *objects)
                                    changeTopContainerVisible(VISIBLE)
                                    Debuger.printfError("***** onEnterFullscreen **** " + objects[0]) //title
                                    Debuger.printfError("***** onEnterFullscreen **** " + objects[1]) //当前全屏player
                                }

                                override fun onAutoComplete(url: String, vararg objects: Any) {
                                    super.onAutoComplete(url, *objects)
                                }

                                override fun onClickStartError(url: String, vararg objects: Any) {
                                    super.onClickStartError(url, *objects)
                                }

                                override fun onQuitFullscreen(url: String, vararg objects: Any) {
                                    super.onQuitFullscreen(url, *objects)
                                    changeTopContainerVisible(INVISIBLE)
                                    Debuger.printfError("***** onQuitFullscreen **** " + objects[0]) //title
                                    Debuger.printfError("***** onQuitFullscreen **** " + objects[1]) //当前非全屏player

                                    orientationUtils.backToProtVideo()
                                }
                            })
                            .setLockClickListener { _, lock ->
                                orientationUtils.isEnable = !lock
                            }
                            .setGSYVideoProgressListener { progress, secProgress, currentPosition, duration ->
                                Debuger.printfLog(
                                    " progress $progress secProgress $secProgress currentPosition $currentPosition duration $duration"
                                )
                            }
                            .build(this)

                        fullscreenButton.setOnClickListener { _ ->
                            orientationUtils.resolveByClick()
                            //changeTopContainerVisible(VISIBLE)

                            startWindowFullscreen(
                                it,
                                true,
                                true
                            )
                        }
                        subTitle = "http://img.cdn.guoshuyu.cn/subtitle2.srt";

                    }
                })
        }

    }
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





