package com.leri.smovbook.mediaRealization

import cn.jzvd.Jzvd
import cn.jzvd.JZMediaInterface
import androidx.media3.exoplayer.ExoPlayer
import timber.log.Timber
import android.os.HandlerThread
import androidx.media3.exoplayer.trackselection.ExoTrackSelection
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.TrackSelector
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.upstream.DefaultAllocator
import androidx.media3.exoplayer.upstream.BandwidthMeter
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.datasource.DefaultDataSourceFactory
import com.leri.smovbook.R
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.Surface
import androidx.media3.common.*
import androidx.media3.common.Player.PositionInfo
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource

/**
 * Created by MinhDV on 5/3/18.
 */
class JZMediaExo(jzvd: Jzvd?) : JZMediaInterface(jzvd), Player.Listener {
    private var simpleExoPlayer: ExoPlayer? = null
    private var callback: Runnable? = null
    private val tag = "JZMediaExo"
    private var previousSeek: Long = 0
    override fun start() {
        simpleExoPlayer!!.playWhenReady = true
    }

    //这里还有 header 没定义 明天要看看 视频播放不了 明天得看看怎么改 现在虽然改了这么久 但是只能提供一个思路
    //https://dwdev.coding.net/p/smovbook/d/SmovBookM/git/tree/Dev_Video_Lib/videoplayer_media3/src/main/java/com/leri/videoplayer_media3/DefaultVideoPlayerController.kt
    override fun prepare() {
        Timber.tag(tag).e("prepare")
        val context = jzvd.context
        release()
        mMediaHandlerThread = HandlerThread("JZVD")
        mMediaHandlerThread.start()
        mMediaHandler = Handler(context.mainLooper) //主线程还是非主线程，就在这里
        handler = Handler(Looper.getMainLooper())

        mMediaHandler.post {
            val videoTrackSelectionFactory: ExoTrackSelection.Factory =
                AdaptiveTrackSelection.Factory()
            val trackSelector: TrackSelector =
                DefaultTrackSelector(context, videoTrackSelectionFactory)

            val loadControl: LoadControl = DefaultLoadControl.Builder()
                .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
                .setBufferDurationsMs(360000, 600000, 1000, 5000)
                .setPrioritizeTimeOverSizeThresholds(false)
                .setTargetBufferBytes(C.LENGTH_UNSET)
                .build()

            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter.Builder(context).build()
            // 2. Create the player
            val renderersFactory: RenderersFactory = DefaultRenderersFactory(context)
            simpleExoPlayer =
                ExoPlayer.Builder(context, renderersFactory).setTrackSelector(trackSelector)
                    .setLoadControl(loadControl).setBandwidthMeter(bandwidthMeter).build()
            // Produces DataSource instances through which media data is loaded.
            val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(context)

            val currUrl = jzvd.jzDataSource.currentUrl.toString()
            val videoSource: MediaSource
            videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                MediaItem.fromUri(
                    Uri.parse(currUrl)
                )
            )
            simpleExoPlayer!!.addListener(this)

            Timber.tag(tag).e("URL Link = %s", currUrl)

            simpleExoPlayer!!.addListener(this)

            val isLoop = jzvd.jzDataSource.looping

            if (isLoop) {
                simpleExoPlayer!!.repeatMode = Player.REPEAT_MODE_ONE
            } else {
                simpleExoPlayer!!.repeatMode = Player.REPEAT_MODE_OFF
            }

            simpleExoPlayer!!.setMediaSource(videoSource)
            simpleExoPlayer!!.playWhenReady = true
            callback = onBufferingUpdate()
            if (jzvd.textureView != null) {
                val surfaceTexture = jzvd.textureView.surfaceTexture
                if (surfaceTexture != null) {
                    simpleExoPlayer!!.setVideoSurface(Surface(surfaceTexture))
                }
            }
        }
    }

    //int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio
    override fun onVideoSizeChanged(videoSize: VideoSize) {
        handler.post {
            jzvd.onVideoSizeChanged(
                (videoSize.width * videoSize.pixelWidthHeightRatio).toInt(),
                videoSize.height
            )
        }
    }

    override fun onRenderedFirstFrame() {
        Timber.tag(tag).e("onRenderedFirstFrame")
    }

    override fun pause() {
        simpleExoPlayer!!.playWhenReady = false
    }

    override fun isPlaying(): Boolean {
        return simpleExoPlayer!!.playWhenReady
    }

    override fun seekTo(time: Long) {
        if (simpleExoPlayer == null) {
            return
        }
        if (time != previousSeek) {
            if (time >= simpleExoPlayer!!.bufferedPosition) {
                jzvd.onStatePreparingPlaying()
            }
            simpleExoPlayer!!.seekTo(time)
            previousSeek = time
            jzvd.seekToInAdvance = time
        }
    }

    override fun release() {
        if (mMediaHandler != null && mMediaHandlerThread != null && simpleExoPlayer != null) { //不知道有没有妖孽
            val tmpHandlerThread = mMediaHandlerThread
            val tmpMediaPlayer: ExoPlayer = simpleExoPlayer as ExoPlayer
            SAVED_SURFACE = null
            mMediaHandler.post {
                tmpMediaPlayer.release() //release就不能放到主线程里，界面会卡顿
                tmpHandlerThread.quit()
            }
            simpleExoPlayer = null
        }
    }

    override fun getCurrentPosition(): Long {
        return if (simpleExoPlayer != null) simpleExoPlayer!!.currentPosition else 0
    }

    override fun getDuration(): Long {
        return if (simpleExoPlayer != null) simpleExoPlayer!!.duration else 0
    }

    override fun setVolume(leftVolume: Float, rightVolume: Float) {
        simpleExoPlayer!!.volume = leftVolume
        simpleExoPlayer!!.volume = rightVolume
    }

    override fun setSpeed(speed: Float) {
        val playbackParameters = PlaybackParameters(speed, 1.0f)
        simpleExoPlayer!!.playbackParameters = playbackParameters
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        Timber.tag(tag).e("onTimelineChanged")
    }

    override fun onTracksChanged(tracks: Tracks) {}
    override fun onIsLoadingChanged(isLoading: Boolean) {
        Timber.tag(tag).e("onLoadingChanged")
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        Timber.tag(tag).e("onPlayerStateChanged$playbackState/ready=$playbackState")
        handler.post {
            when (playbackState) {
                Player.STATE_IDLE -> {}
                Player.STATE_BUFFERING -> {
                    jzvd.onStatePreparingPlaying()
                    handler.post(callback!!)
                }
                Player.STATE_ENDED -> {
                    jzvd.onCompletion()
                }
                Player.STATE_READY -> {}
            }
        }
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        if (playWhenReady) {
            jzvd.onStatePlaying()
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {}
    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
    override fun onPlayerError(error: PlaybackException) {
        Timber.tag(tag).e("onPlayerError%s", error.toString())
        handler.post { jzvd.onError(1000, 1000) }
    }

    override fun onPositionDiscontinuity(
        oldPosition: PositionInfo,
        newPosition: PositionInfo,
        reason: Int
    ) {
        handler.post { jzvd.onSeekComplete() }
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
    override fun setSurface(surface: Surface) {
        if (simpleExoPlayer != null) {
            simpleExoPlayer!!.setVideoSurface(surface)
        } else {
            Timber.tag("AGVideo").e("simpleExoPlayer为空")
        }
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        if (SAVED_SURFACE == null) {
            SAVED_SURFACE = surface
            prepare()
        } else {
            jzvd.textureView.setSurfaceTexture(SAVED_SURFACE)
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    private inner class onBufferingUpdate : Runnable {
        override fun run() {
            if (simpleExoPlayer != null) {
                val percent = simpleExoPlayer!!.bufferedPercentage
                handler.post { jzvd.setBufferProgress(percent) }
                if (percent < 100) {
                    handler.postDelayed(callback!!, 300)
                } else {
                    handler.removeCallbacks(callback!!)
                }
            }
        }
    }
}