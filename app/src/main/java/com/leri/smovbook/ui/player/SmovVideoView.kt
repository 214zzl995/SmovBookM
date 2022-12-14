package com.leri.smovbook.ui.player

import android.animation.AnimatorInflater
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.Dimension.DP
import androidx.compose.ui.platform.ComposeView
import androidx.core.animation.addListener
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SeekParameters
import com.google.android.exoplayer2.text.CueGroup
import com.google.android.exoplayer2.ui.CaptionStyleCompat
import com.google.android.exoplayer2.ui.SubtitleView
import com.leri.smovbook.R
import com.leri.smovbook.ui.player.exosubtitle.GSYExoSubTitlePlayerManager
import com.leri.smovbook.ui.player.exosubtitle.GSYExoSubTitleVideoManager
import com.leri.smovbook.ui.player.utils.OrientationUtils
import com.leri.smovbook.ui.smovDetail.getActivity
import com.leri.smovbook.ui.theme.SmovBookMTheme
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
    private var sCover: String = ""

    val title: String get() = sTitle
    val url: String get() = sUrl

    private var gsyVideoOption = GSYVideoOptionBuilder()

    private lateinit var mBottomFunction: ViewGroup

    private lateinit var mDialog: ViewGroup

    private lateinit var mClarityDialog: ViewGroup

    private lateinit var mClarityButton: ViewGroup

    private lateinit var mSpeedDialog: ViewGroup

    private lateinit var mSpeedButton: ViewGroup

    private lateinit var mOtherButton: ViewGroup

    private lateinit var mOtherDialog: ViewGroup

    fun smovInit(title: String, url: String, subTitle: String?, cover: String) {
        this.sTitle = title
        this.sUrl = url
        this.subTitle = subTitle
        this.sCover = cover


        //????????????
        val imageView = ImageView(context)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.load(cover) {
            crossfade(true)
            error(R.drawable.ic_error)
        }

        mTitleTextView.visibility = GONE
        mBackButton.visibility = GONE
        orientationUtils.isEnable = false
        gsyVideoOption
            .setThumbImageView(imageView)
            .setIsTouchWiget(false)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setAutoFullWithSize(false)
            .setShowFullAnimation(false)
            .setNeedLockFull(true)
            .setUrl(sUrl)
            .setSeekRatio(2.5f)
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


    constructor(context: Context?) : super(context) {
        this.orientationUtils = context!!.getActivity()?.let { OrientationUtils(it, this) }!!
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context?, title: String, url: String, subTitle: String?) : super(context) {
        this.sTitle = title
        this.sUrl = url
        this.subTitle = subTitle
        this.orientationUtils = context!!.getActivity()?.let { OrientationUtils(it, this) }!!
    }

    fun setSubtitleViewTextSize(size: Float) {
        mSubtitleView.setFixedTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
    }

    override fun init(context: Context) {
        super.init(context)
        fullscreenButton.setOnClickListener(this)

        mSubtitleView = findViewById(R.id.sub_title_view)
        mSubtitleView.setStyle(
            CaptionStyleCompat(
                Color.WHITE,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                CaptionStyleCompat.EDGE_TYPE_NONE,
                CaptionStyleCompat.EDGE_TYPE_NONE,
                null
            )
        )

        setSubtitleViewTextSize(14F)

        mBottomFunction = findViewById(R.id.layout_bottom_function)
        mDialog = findViewById(R.id.dialog)

        mClarityDialog = findViewById(R.id.clarity_dialog)
        mClarityButton = findViewById(R.id.clarity)
        mSpeedButton = findViewById(R.id.speed)
        mSpeedDialog = findViewById(R.id.speed_dialog)
        mOtherButton = findViewById(R.id.other)
        mOtherDialog = findViewById(R.id.other_dialog)

        mDialog.setOnClickListener(this)
        mSpeedButton.setOnClickListener(this)
        mClarityButton.setOnClickListener(this)
        mOtherButton.setOnClickListener(this)

        mBottomContainer.setPadding(5, 0, 20, 0)

        val greeting = findViewById<ComposeView>(R.id.compose_dialog)

        //Compose??????View ????????????????????????????????? ?????????View??????????????????
        greeting.setContent {
            SmovBookMTheme {
                ComposeDialog()
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.video_layout_subtitle
    }

    override fun updateStartImage() {
        if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            when (mCurrentState) {
                CURRENT_STATE_PLAYING -> {
                    imageView.setImageResource(R.drawable.video_click_pause_selector_smovbook)
                }
                CURRENT_STATE_ERROR -> {
                    imageView.setImageResource(R.drawable.video_click_play_selector_smovbook)
                }
                else -> {
                    imageView.setImageResource(R.drawable.video_click_play_selector_smovbook)
                }
            }
        }
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
        when (v.id) {
            R.id.fullscreen -> {
                /**
                 * ???????????????????????????
                 * orientationUtils.resolveByClick() ????????????????????????????????????
                 */
                orientationUtils.resolveByClick()
                startWindowFullscreen(context, actionBar = true, statusBar = true)
            }
            R.id.clarity -> {
                showDialog(mClarityDialog)
                //?????? GSYVideoControlView 1097??? 1026??? 829??? ?????????????????? ??????????????? ?????? resolveUIState
                resolveUIState(CURRENT_STATE_PAUSE)
            }
            R.id.other -> {
                showDialog(mOtherDialog)
                resolveUIState(CURRENT_STATE_PAUSE)
            }
            R.id.speed -> {
                showDialog(mSpeedDialog)
                resolveUIState(CURRENT_STATE_PAUSE)
            }
            R.id.dialog -> {
                disableDialog(getShowDialog())
                resolveUIState(CURRENT_STATE_PLAYING)
            }
        }

        super.onClick(v)
    }

    /**
     * ???????????????????????? ??????????????????
     */
    private fun showDialog(v: ViewGroup) {
        mDialog.visibility = VISIBLE
        v.visibility = VISIBLE
        val animator = AnimatorInflater.loadAnimator(context, R.animator.dialog_in)
        animator.setTarget(v)
        animator.start()
    }

    /**
     * ???????????????????????? ????????????
     */
    private fun disableDialog(v: ViewGroup) {
        val animator = AnimatorInflater.loadAnimator(context, R.animator.dialog_out)
        animator.addListener(onEnd = {
            v.visibility = GONE
            mDialog.visibility = GONE
        })
        animator.setTarget(v)
        animator.start()
    }

    private fun getShowDialog(): ViewGroup {
        return if (mOtherDialog.visibility == VISIBLE) {
            mOtherDialog
        } else if (mSpeedDialog.visibility == VISIBLE) {
            mSpeedDialog
        } else {
            mClarityDialog
        }
    }


    override fun onCues(cueGroup: CueGroup) {
        mSubtitleView.setCues(cueGroup.cues)
    }

    /**********???????????? GSYVideoPlayer ??? ?????? SubtitleView ???????????? */
    override fun startWindowFullscreen(
        context: Context,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer {
        val gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar)
        val gsyExoSubTitleVideoView = gsyBaseVideoPlayer as SmovVideoView

        (GSYExoSubTitleVideoManager.instance().player as GSYExoSubTitlePlayerManager).addTextOutputPlaying(gsyExoSubTitleVideoView)

        gsyBaseVideoPlayer.setSubtitleViewTextSize(24f)

        gsyBaseVideoPlayer.mBottomFunction.visibility = VISIBLE
        gsyBaseVideoPlayer.mBottomContainer.setPadding(20, 0, 10, 0)
        return gsyBaseVideoPlayer
    }

    override fun resolveNormalVideoShow(oldF: View, vp: ViewGroup, gsyVideoPlayer: GSYVideoPlayer) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer)

        val gsyExoSubTitleVideoView = gsyVideoPlayer as SmovVideoView
        (GSYExoSubTitleVideoManager.instance().player as GSYExoSubTitlePlayerManager).removeTextOutput(
            gsyExoSubTitleVideoView
        )

        gsyExoSubTitleVideoView.setSubtitleViewTextSize(14f)

        //?????????????????????????????????????????????
        orientationUtils.resolveByClick()
        gsyExoSubTitleVideoView.mBottomFunction.visibility = GONE
        gsyExoSubTitleVideoView.mBottomContainer.setPadding(5, 0, 5, 0)
    }

    /**********????????????GSYVideoPlayer???GSYVideoViewBridge???????????? */
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

        //????????????????????????????????????
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
        println("????????????")
        Debuger.printfError("***** onEnterFullscreen **** " + objects[0]) //title
        Debuger.printfError("***** onEnterFullscreen **** " + objects[1]) //????????????player
    }

    override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
        println("????????????")
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

