package com.leri.videoplayer_media3

import android.os.Parcelable
import com.leri.videoplayer_media3.util.getDurationString
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.math.abs

@Parcelize
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
) : Parcelable

@Parcelize
data class DraggingProgress(
    val finalTime: Float,
    val diffTime: Float
) : Parcelable {
    val progressText: String
        get() = "${getDurationString(finalTime.toLong(), false)} " +
                "[${if (diffTime < 0) "-" else "+"}${
                    getDurationString(
                        abs(diffTime.toLong()),
                        false
                    )
                }]"
}

enum class QuickSeekDirection {
    None,
    Rewind,
    Forward
}

@Parcelize
data class QuickSeekAction(
    val direction: QuickSeekDirection
) : Parcelable {
    // Each action is unique
    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        return Objects.hash(direction)
    }

    companion object {
        fun none() = QuickSeekAction(QuickSeekDirection.None)
        fun forward() = QuickSeekAction(QuickSeekDirection.Forward)
        fun rewind() = QuickSeekAction(QuickSeekDirection.Rewind)
    }
}