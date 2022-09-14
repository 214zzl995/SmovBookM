package com.leri.videoplayer_media3

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.Util
import androidx.media3.datasource.*
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.leri.videoplayer_media3.util.FlowDebouncer
import com.leri.videoplayer_media3.util.set
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicBoolean


@OptIn(InternalCoroutinesApi::class)
internal class DefaultVideoPlayerController(
    private val context: Context,
    private val initialState: VideoPlayerState,
    private val coroutineScope: CoroutineScope
) : VideoPlayerController {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<VideoPlayerState>
        get() = _state.asStateFlow()

    private val active = context as Activity

    /**
     * Some properties in initial state are not applicable until player is ready.
     * These are kept in this container. Once the player is ready for the first time,
     * they are applied and removed.
     */
    private var initialStateRunner: (() -> Unit)? = {
        exoPlayer.seekTo(initialState.currentPosition)
    }

    fun <T> currentState(filter: (VideoPlayerState) -> T): T {
        return filter(_state.value)
    }

    private fun getStateValue(): VideoPlayerState {
        return _state.value
    }

    @Composable
    fun collect(): State<VideoPlayerState> {
        return _state.collectAsState()
    }

    @Composable
    fun <T> collect(filter: VideoPlayerState.() -> T): State<T> {
        return remember(filter) { _state.map { it.filter() } }.collectAsState(initial = getStateValue().filter())
    }

    var videoPlayerBackgroundColor: Int = DefaultVideoPlayerBackgroundColor.value.toInt()
        set(value) {
            field = value
            playerView?.setBackgroundColor(value)
        }

    private lateinit var source: VideoPlayerSource
    private var playerView: PlayerView? = null

    private var updateDurationAndPositionJob: Job? = null
    private val playerEventListener = object : Player.Listener {

        //状态监听准备好后 开始播放
        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            _state.set {
                copy(
                    isPlaying = playWhenReady,
                )
            }
        }

        //当前视频状态
        override fun onPlaybackStateChanged(playbackState: Int) {
            //已经知道每次都能监听到 但是状态没有实时的出现
            println("状态监听${PlaybackState.of(playbackState)} ")
            if (PlaybackState.of(playbackState) == PlaybackState.READY) {
                initialStateRunner = initialStateRunner?.let {
                    it.invoke()
                    null
                }

                updateDurationAndPositionJob?.cancel()
                updateDurationAndPositionJob = coroutineScope.launch {
                    while (this.isActive) {
                        updateDurationAndPosition()
                        delay(250)
                    }
                }
            }
            _state.set {
                copy(
                    playbackState = PlaybackState.of(playbackState)
                )
            }

        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)

            _state.set {
                copy(videoSize = videoSize.width.toFloat() to videoSize.height.toFloat())
            }
        }

    }


    /**
     * Internal exoPlayer instance
     */
    private val exoPlayer = ExoPlayer.Builder(context)
        .build()
        .apply {
            playWhenReady = initialState.isPlaying
            addListener(playerEventListener)
        }

    /**
     * Not so efficient way of showing preview in video slider.
     */
    private val previewExoPlayer = ExoPlayer.Builder(context)
        .build()
        .apply {
            playWhenReady = false
        }

    private val previewSeekDebouncer = FlowDebouncer<Long>(200L)

    init {
        exoPlayer.playWhenReady = initialState.isPlaying

        coroutineScope.launch {
            previewSeekDebouncer.collect { position ->
                previewExoPlayer.seekTo(position)
            }
        }
    }

    /**
     * A flag to indicate whether source is already set and waiting for
     * playerView to become available.
     */
    private val waitPlayerViewToPrepare = AtomicBoolean(false)

    override fun play() {
        if (exoPlayer.playbackState == Player.STATE_ENDED) {
            exoPlayer.seekTo(0)
        }
        exoPlayer.playWhenReady = true
    }

    override fun pause() {
        exoPlayer.playWhenReady = false
    }

    override fun playPauseToggle() {
        if (exoPlayer.isPlaying) pause()
        else play()
    }

    override fun quickSeekForward() {
        if (_state.value.quickSeekAction.direction != QuickSeekDirection.None) {
            // Currently animating
            return
        }
        val target = (exoPlayer.currentPosition + 10_000).coerceAtMost(exoPlayer.duration)
        exoPlayer.seekTo(target)
        updateDurationAndPosition()
        _state.set { copy(quickSeekAction = QuickSeekAction.forward()) }
    }

    override fun quickSeekRewind() {
        if (_state.value.quickSeekAction.direction != QuickSeekDirection.None) {
            // Currently animating
            return
        }
        val target = (exoPlayer.currentPosition - 10_000).coerceAtLeast(0)
        exoPlayer.seekTo(target)
        updateDurationAndPosition()
        _state.set { copy(quickSeekAction = QuickSeekAction.rewind()) }
    }

    override fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
        updateDurationAndPosition()
    }

    override fun setSource(source: VideoPlayerSource) {
        this.source = source
        if (playerView == null) {
            waitPlayerViewToPrepare.set(true)
        } else {
            prepare()
        }
    }

    fun enableGestures(isEnabled: Boolean) {
        _state.set { copy(gesturesEnabled = isEnabled) }
    }

    fun enableControls(enabled: Boolean) {
        _state.set { copy(controlsEnabled = enabled) }
    }

    fun showControls() {
        _state.set { copy(controlsVisible = true) }
    }

    fun hideControls() {
        _state.set { copy(controlsVisible = false) }
    }

    fun setDraggingProgress(draggingProgress: DraggingProgress?) {
        _state.set { copy(draggingProgress = draggingProgress) }
    }

    fun setQuickSeekAction(quickSeekAction: QuickSeekAction) {
        _state.set { copy(quickSeekAction = quickSeekAction) }
    }

    private fun updateDurationAndPosition() {
        _state.set {
            copy(
                duration = exoPlayer.duration.coerceAtLeast(0),
                currentPosition = exoPlayer.currentPosition.coerceAtLeast(0),
                secondaryProgress = exoPlayer.bufferedPosition.coerceAtLeast(0)
            )
        }
    }

    private fun prepare() {
        fun createVideoSource(): MediaSource {
            val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory().apply {
                this.setUserAgent(Util.getUserAgent(context, context.packageName))
            }

            return when (val source = source) {
                is VideoPlayerSource.Raw -> {
                    ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(
                            MediaItem.fromUri(
                                RawResourceDataSource.buildRawResourceUri(
                                    source.resId
                                )
                            )
                        )
                }
                is VideoPlayerSource.Network -> {
                    ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(Uri.parse(source.url)))
                }
            }
        }

        exoPlayer.setMediaSource(createVideoSource())
        previewExoPlayer.setMediaSource(createVideoSource())

        exoPlayer.prepare()
        previewExoPlayer.prepare()
    }

    fun playerViewAvailable(playerView: PlayerView) {
        this.playerView = playerView
        playerView.player = exoPlayer
        playerView.setBackgroundColor(videoPlayerBackgroundColor)

        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        playerView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (waitPlayerViewToPrepare.compareAndSet(true, false)) {
            prepare()
        }
    }

    fun previewPlayerViewAvailable(playerView: PlayerView) {
        playerView.player = previewExoPlayer
    }

    fun previewSeekTo(position: Long) {
        // position is very accurate. Thumbnail doesn't have to be.
        // Roll to the nearest "even" integer.
        val seconds = position.toInt() / 1000
        val nearestEven = (seconds - seconds.rem(2)).toLong()
        coroutineScope.launch {
            previewSeekDebouncer.put(nearestEven * 1000)
        }
    }

    override fun setFullScreen() {
        if (!_state.value.fullScreen) {
            active.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            _state.set {
                copy(
                    fullScreen = true
                )
            }
        } else {
            active.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            _state.set {
                copy(
                    fullScreen = false
                )
            }
        }

    }

    override fun reset() {
        exoPlayer.stop()
        previewExoPlayer.stop()
    }
}

val DefaultVideoPlayerBackgroundColor = Color.Black