package com.leri.smovbook.ui.videoplayer

import kotlinx.coroutines.flow.StateFlow


//对于全屏状态的构思 应该要在这里设置一个状态给上级 对于其他控件的隐藏有两个想法 1.这个状态只负责页面的拉伸 而不负责其他界面的隐藏 2.由这个界面去覆盖在所有界面的上层 当返回时 退出全屏
interface VideoPlayerController {

    fun setSource(source: VideoPlayerSource)

    fun play()

    fun pause()

    fun playPauseToggle()

    fun quickSeekForward()

    fun quickSeekRewind()

    fun seekTo(position: Long)

    fun reset()

    val state: StateFlow<VideoPlayerState>

    fun setFullScreen()
}