package com.leri.smovbook.ui.videoplayer

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

internal val LocalVideoPlayerController =
    compositionLocalOf<DefaultVideoPlayerController> { error("VideoPlayerController is not initialized") }

@Composable
fun rememberVideoPlayerController(
    source: VideoPlayerSource? = null
): VideoPlayerController {
    val context = LocalContext.current as Activity

    val coroutineScope = rememberCoroutineScope()

    return rememberSaveable(
        context, coroutineScope,
        saver = object : Saver<DefaultVideoPlayerController, VideoPlayerState> {
            override fun restore(value: VideoPlayerState): DefaultVideoPlayerController {
                return DefaultVideoPlayerController(
                    context = context,
                    initialState = value,
                    coroutineScope = coroutineScope
                )
            }

            override fun SaverScope.save(value: DefaultVideoPlayerController): VideoPlayerState {
                return value.currentState { it }
            }
        },
        init = {
            DefaultVideoPlayerController(
                context = context,
                initialState = VideoPlayerState(),
                coroutineScope = coroutineScope
            ).apply {
                source?.let { setSource(it) }
            }
        }
    )
}

@Composable
fun VideoPlayer(
    videoPlayerController: VideoPlayerController,
    modifier: Modifier = Modifier,
    controlsEnabled: Boolean = true,
    gesturesEnabled: Boolean = true,
    backgroundColor: Color = Color.Black
) {
    require(videoPlayerController is DefaultVideoPlayerController) {
        "Use [rememberVideoPlayerController] to create an instance of [VideoPlayerController]"
    }

    SideEffect {
        videoPlayerController.videoPlayerBackgroundColor = backgroundColor.value.toInt()
        videoPlayerController.enableControls(controlsEnabled)
        videoPlayerController.enableGestures(gesturesEnabled)
    }

    CompositionLocalProvider(
        LocalContentColor provides Color.White,
        LocalVideoPlayerController provides videoPlayerController
    ) {
        val aspectRatio by videoPlayerController.collect { videoSize.first / videoSize.second }

        Box(
            modifier = Modifier
                .background(color = backgroundColor)
                .aspectRatio(aspectRatio)
                .then(modifier)
        ) {
            PlayerSurface {
                videoPlayerController.playerViewAvailable(it)
            }

            MediaControlGestures(modifier = Modifier.matchParentSize())
            MediaControlButtons(modifier = Modifier.matchParentSize())
            ProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

//监听方向变化 可以在这里 通过方向变化动态修改 播放器的状态
@Composable
fun ConfigChangeExample() {
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Text("Landscape")
        }
        else -> {
            Text("Portrait")
        }
    }
}