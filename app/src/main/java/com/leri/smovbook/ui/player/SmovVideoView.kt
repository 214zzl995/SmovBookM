package com.leri.smovbook.ui.player

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import androidx.compose.runtime.State
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SeekParameters
import com.google.android.exoplayer2.text.CueGroup
import com.google.android.exoplayer2.ui.CaptionStyleCompat
import com.google.android.exoplayer2.ui.SubtitleView
import com.leri.smovbook.R
import com.leri.smovbook.models.network.NetworkState
import com.leri.smovbook.ui.player.exosubtitle.GSYExoSubTitlePlayerManager
import com.leri.smovbook.ui.player.exosubtitle.GSYExoSubTitleVideoManager
import com.leri.smovbook.ui.player.utils.OrientationUtils
import com.leri.smovbook.ui.smovDetail.getActivity
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager

class SmovVideoView : NormalGSYVideoPlayer, Player.Listener,
    View.OnClickListener, VideoAllCallBack {

    private lateinit var mSubtitleView: SubtitleView
    var subTitle: String? = null
    lateinit var orientationUtils: OrientationUtils

    private var sTitle: String = ""
    private var sUrl: String = ""

    val title: String get() = sTitle
    val url: String get() = sUrl

    private var gsyVideoOption = GSYVideoOptionBuilder()


    fun smovInit() {
        mTitleTextView.visibility = GONE
        mBackButton.visibility = GONE
        orientationUtils.isEnable = false
        gsyVideoOption
            .setIsTouchWiget(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setAutoFullWithSize(false)
            .setShowFullAnimation(false)
            .setNeedLockFull(true)
            .setUrl(sUrl)
            .setCacheWithPlay(false)
            .setVideoAllCallBack(this)
            .setVideoTitle(sTitle).setLockClickListener { _, lock ->
                orientationUtils.isEnable = !lock
            }
            .setEnlargeImageRes(R.drawable.ic_add_to_booksheelf)
            .setShrinkImageRes(R.drawable.ic_add_to_booksheelf)
            .setGSYVideoProgressListener { progress, secProgress, currentPosition, duration ->
                Debuger.printfLog(
                    " progress $progress secProgress $secProgress currentPosition $currentPosition duration $duration"
                )
            }
            .build(this)
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context?, title: String, url: String) : super(context) {
        this.sTitle = title
        this.sUrl = url
        this.orientationUtils = context!!.getActivity()?.let { OrientationUtils(it, this) }!!
    }

    override fun init(context: Context) {
        super.init(context)
        fullscreenButton.setOnClickListener(this)
        mSubtitleView = findViewById(R.id.sub_title_view)
        mSubtitleView.setStyle(
            CaptionStyleCompat(
                Color.RED,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                CaptionStyleCompat.EDGE_TYPE_NONE,
                CaptionStyleCompat.EDGE_TYPE_NONE,
                null
            )
        )
        mSubtitleView.setFixedTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
    }

    override fun getLayoutId(): Int {
        return R.layout.video_layout_subtitle
    }

    override fun startPrepare() {
        if (gsyVideoManager.listener() != null) {
            gsyVideoManager.listener().onCompletion()
        }
        if (mVideoAllCallBack != null) {
            Debuger.printfLog("onStartPrepared")
            mVideoAllCallBack.onStartPrepared(mOriginUrl, mTitle, this)
        }
        gsyVideoManager.setListener(this)
        gsyVideoManager.playTag = mPlayTag
        gsyVideoManager.playPosition = mPlayPosition
        mAudioManager.requestAudioFocus(
            onAudioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        )
        try {
            if (mContext is Activity) {
                (mContext as Activity).window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mBackUpPlayingBufferState = -1
        gsyVideoManager.prepare(
            mUrl,
            subTitle,
            this,
            if (mMapHeadData == null) HashMap() else mMapHeadData,
            mLooping,
            mSpeed,
            mCache,
            mCachePath,
            mOverrideExtension
        )
        setStateAndUi(CURRENT_STATE_PREPAREING)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.fullscreen) {
            /**
             * 确定出现错误的位置
             * orientationUtils.resolveByClick() 没有成功的把屏幕旋转过来
             */
            orientationUtils.resolveByClick()
            startWindowFullscreen(v.context, actionBar = true, statusBar = true)
        }
        super.onClick(v)
    }

    override fun onCues(cueGroup: CueGroup) {
        mSubtitleView.setCues(cueGroup.cues)
    }

    /**********以下重载 GSYVideoPlayer 的 全屏 SubtitleView 相关实现 */
    override fun startWindowFullscreen(
        context: Context,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer {
        val gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar)
        val gsyExoSubTitleVideoView = gsyBaseVideoPlayer as SmovVideoView
        (GSYExoSubTitleVideoManager.instance().player as GSYExoSubTitlePlayerManager)
            .addTextOutputPlaying(gsyExoSubTitleVideoView)
        return gsyBaseVideoPlayer
    }

    override fun resolveNormalVideoShow(oldF: View, vp: ViewGroup, gsyVideoPlayer: GSYVideoPlayer) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer)
        //加了这个横竖屏旋转正常了。。。
        orientationUtils.resolveByClick()
        val gsyExoSubTitleVideoView = gsyVideoPlayer as SmovVideoView
        (GSYExoSubTitleVideoManager.instance().player as GSYExoSubTitlePlayerManager).removeTextOutput(
            gsyExoSubTitleVideoView
        )
    }

    /**********以下重载GSYVideoPlayer的GSYVideoViewBridge相关实现 */
    override fun getGSYVideoManager(): GSYExoSubTitleVideoManager {
        GSYExoSubTitleVideoManager.instance().initContext(context.applicationContext)
        return GSYExoSubTitleVideoManager.instance()
    }

    override fun backFromFull(context: Context): Boolean {
        return GSYExoSubTitleVideoManager.backFromWindowFull(context)
    }

    override fun releaseVideos() {
        GSYExoSubTitleVideoManager.releaseAllVideos()
    }

    override fun getFullId(): Int {
        return GSYExoSubTitleVideoManager.FULLSCREEN_ID
    }

    override fun getSmallId(): Int {
        return GSYExoSubTitleVideoManager.SMALL_ID
    }

    override fun onStartPrepared(url: String?, vararg objects: Any?) {

    }

    override fun onPrepared(url: String, vararg objects: Any) {
        Debuger.printfError("***** onPrepared **** " + objects[0])
        Debuger.printfError("***** onPrepared **** " + objects[1])

        orientationUtils.isEnable = isRotateWithSystem

        //外部辅助的旋转，帮助全屏
        if (gsyVideoManager.player is Exo2PlayerManager) {
            (gsyVideoManager.player as Exo2PlayerManager).setSeekParameter(
                SeekParameters.NEXT_SYNC
            )
            Debuger.printfError("***** setSeekParameter **** ")
        }
    }

    override fun onClickStartIcon(url: String?, vararg objects: Any?) {

    }

    override fun onClickStartError(url: String?, vararg objects: Any?) {

    }

    override fun onClickStop(url: String?, vararg objects: Any?) {

    }

    override fun onClickStopFullscreen(url: String?, vararg objects: Any?) {

    }

    override fun onClickResume(url: String?, vararg objects: Any?) {

    }

    override fun onClickResumeFullscreen(url: String?, vararg objects: Any?) {

    }

    override fun onClickSeekbar(url: String?, vararg objects: Any?) {

    }

    override fun onClickSeekbarFullscreen(url: String?, vararg objects: Any?) {

    }

    override fun onAutoComplete(url: String?, vararg objects: Any?) {

    }

    override fun onComplete(url: String?, vararg objects: Any?) {

    }

    override fun onEnterFullscreen(url: String, vararg objects: Any) {
        println("进入全屏")
        Debuger.printfError("***** onEnterFullscreen **** " + objects[0]) //title
        Debuger.printfError("***** onEnterFullscreen **** " + objects[1]) //当前全屏player
    }

    override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
        println("退出全屏")
    }

    override fun onQuitSmallWidget(url: String?, vararg objects: Any?) {

    }

    override fun onEnterSmallWidget(url: String?, vararg objects: Any?) {

    }

    override fun onTouchScreenSeekVolume(url: String?, vararg objects: Any?) {

    }

    override fun onTouchScreenSeekPosition(url: String?, vararg objects: Any?) {

    }

    override fun onTouchScreenSeekLight(url: String?, vararg objects: Any?) {

    }

    override fun onPlayError(url: String?, vararg objects: Any?) {

    }

    override fun onClickStartThumb(url: String?, vararg objects: Any?) {

    }

    override fun onClickBlank(url: String?, vararg objects: Any?) {

    }

    override fun onClickBlankFullscreen(url: String?, vararg objects: Any?) {

    }
}