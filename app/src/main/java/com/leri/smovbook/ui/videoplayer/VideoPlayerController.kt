package com.leri.smovbook.ui.videoplayer

import kotlinx.coroutines.flow.StateFlow

interface VideoPlayerController{

    fun setSource(source: VideoPlayerSource)

    fun play()

    fun pause()

    fun playPauseToggle()

    fun quickSeekForward()

    fun quickSeekRewind()

    fun seekTo(position: Long)

    fun reset()

    fun setFullScreen()

    val state: StateFlow<VideoPlayerState>

}