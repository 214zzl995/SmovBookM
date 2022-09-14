package com.leri.videoplayer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun ProgressIndicator(
    modifier: Modifier = Modifier
) {
    val controller = LocalVideoPlayerController.current
    val videoPlayerUiState by controller.collect()

    val appearAlpha = remember { Animatable(0f) }

    val controlsVisible by controller.collect { controlsVisible }

    val fullScreen by controller.collect { fullScreen }

    val (controlsExistOnUITree, setControlsExistOnUITree) = remember(controlsVisible) {
        mutableStateOf(true)
    }

    LaunchedEffect(controlsVisible) {
        appearAlpha.animateTo(
            targetValue = if (controlsVisible) 1f else 0f,
            animationSpec = tween(
                durationMillis = 250,
                easing = LinearEasing
            )
        )
        setControlsExistOnUITree(controlsVisible)
    }
    if (!fullScreen || (fullScreen && controlsVisible)) {
        with(videoPlayerUiState) {
            SeekBar(
                progress = currentPosition,
                max = duration,
                enabled = controlsVisible && controlsEnabled,
                onSeek = {
                    controller.previewSeekTo(it)
                },
                onSeekStopped = {
                    controller.seekTo(it)
                },
                secondaryProgress = secondaryProgress,
                seekerPopup = {
                    PlayerSurface(
                        modifier = Modifier
                            .height(48.dp)
                            .width(48.dp * videoSize.first / videoSize.second)
                            .background(Color.DarkGray)
                    ) {
                        controller.previewPlayerViewAvailable(it)
                    }
                },
                modifier = modifier
            )
        }
    }

}