package com.leri.videoplayer_media3

import androidx.media3.common.Player.*


enum class PlaybackState(val value: Int) {

    IDLE(STATE_IDLE),
    BUFFERING(STATE_BUFFERING),
    READY(STATE_READY),
    ENDED(STATE_ENDED);

    companion object {
        fun of(value: Int): PlaybackState {
            return values().first { it.value == value }
        }
    }
}