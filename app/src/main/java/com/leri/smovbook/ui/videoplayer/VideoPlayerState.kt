package com.leri.smovbook.ui.videoplayer

import androidx.lifecycle.ViewModel

data class VideoPlayerState(
    val isPlaying: Boolean = true,
    val controlsVisible: Boolean = true,
    val controlsEnabled: Boolean = true,
    val gesturesEnabled: Boolean = true,
    val fullScreen: Boolean = false,
    val duration: Long = 1L,
    val currentPosition: Long = 1L,
    val secondaryProgress: Long = 1L,
    val videoSize: Pair<Float, Float> = 1920f to 1080f,
    val draggingProgress: DraggingProgress? = null,
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val quickSeekAction: QuickSeekAction = QuickSeekAction.none()
) : ViewModel()