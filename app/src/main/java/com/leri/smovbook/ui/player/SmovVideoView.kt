package com.leri.smovbook.ui.player

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.animation.addListener
import androidx.media3.common.Player
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.SubtitleView
import coil.load
import com.airbnb.lottie.LottieAnimationView
import com.leri.smovbook.R
import com.leri.smovbook.ui.player.exosubtitle.GSYExoSubTitlePlayerManager
import com.leri.smovbook.ui.player.exosubtitle.GSYExoSubTitleVideoManager
import com.leri.smovbook.ui.player.utils.OrientationUtils
import com.leri.smovbook.ui.smovDetail.getActivity
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack
import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import moe.codeest.enviews.ENDownloadView
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import kotlin.math.abs


open class SmovVideoView : StandardGSYVideoPlayer, Player.Listener,
    View.OnClickListener, VideoAllCallBack {

    private lateinit var mSubtitleView: SubtitleView
    var subTitle: String? = null
    private lateinit var orientationUtils: OrientationUtils

    private var sTitle: String = ""
    private var sUrl: String = ""
    private var sCover: String = ""
    private var sProgressBarStartCurrentPosition: Long = 0

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

    private lateinit var mLottieStart: LottieAnimationView

    private lateinit var valueAnimator: ValueAnimator

    private lateinit var animatorUpdateListener: ValueAnimator.AnimatorUpdateListener

    private lateinit var mDialogBrightnessProgressBar: ProgressBar

    private lateinit var mDialogDiffTime: TextView

    private var mVideoStateBackUp: Int = CURRENT_STATE_NORMAL

    private val mStopOnChangeProcess: Boolean = true

    fun smovInit(title: String, url: String, subTitle: String?, cover: String) {
        this.sTitle = title
        this.sUrl = url
        this.subTitle = subTitle
        this.sCover = cover


        //增加封面
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
            .setSeekRatio(8f)
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


    @RequiresApi(Build.VERSION_CODES.R)
    constructor(context: Context?) : super(context) {
        this.orientationUtils = context!!.getActivity()?.let { OrientationUtils(it, this) }!!
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, title: String, url: String, subTitle: String?) : super(context) {
        this.sTitle = title
        this.sUrl = url
        this.subTitle = subTitle
        this.orientationUtils = context!!.getActivity()?.let { OrientationUtils(it, this) }!!
    }

    @UnstableApi
    fun setSubtitleViewTextSize(size: Float) {
        mSubtitleView.setFixedTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
    }

    @UnstableApi
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

        mLottieStart = findViewById(R.id.lottie_start)

        mDialog.setOnClickListener(this)
        mSpeedButton.setOnClickListener(this)
        mClarityButton.setOnClickListener(this)
        mOtherButton.setOnClickListener(this)

        mLottieStart.setOnClickListener(this)

        mBottomContainer.setPadding(5, 0, 20, 0)

        valueAnimator = ValueAnimator.ofFloat(1f, 0f)
        animatorUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
            mLottieStart.progress =
                animation.animatedValue as Float
        }


        /**
         * 此代码仅做测试 傻逼才这么写
         * xml就管xml compose就管xml就行
         *
        private var changeState: (Int) -> Unit = {}


        val composeStart = findViewById<ComposeView>(R.id.compose_start)
        composeStart.setContent {
        var state by rememberSaveable { mutableStateOf(mCurrentState) }
        changeState = {
        state = it
        }
        LaunchedEffect(state) {
        println("测试当值变更是否会触发")
        }
        SmovBookMTheme {
        ComposeStart(state = mCurrentState)
        }
         */
    }

    override fun getLayoutId(): Int {
        return R.layout.video_layout_subtitle
    }


    override fun updateStartImage() {
        if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            when (mCurrentState) {
                CURRENT_STATE_PLAYING -> {
                    imageView.setImageResource(R.drawable.ic_pause)
                }

                CURRENT_STATE_ERROR -> {
                    imageView.setImageResource(R.drawable.ic_play)
                }

                else -> {
                    imageView.setImageResource(R.drawable.ic_play)
                }
            }
        }
    }

    override fun lockTouchLogic() {
        if (mLockCurScreen) {
            mLockScreen.setImageResource(R.drawable.ic_unlock)
            mLockCurScreen = false
        } else {
            mLockScreen.setImageResource(R.drawable.ic_lock)
            mLockCurScreen = true
            hideAllWidget()
        }
    }

    /**
     * 显示音量调节dialog 处理数据
     */
    override fun showVolumeDialog(deltaY: Float, volumePercent: Int) {
        if (mVolumeDialog == null) {
            val localView = LayoutInflater.from(activityContext).inflate(
                volumeLayoutId, null
            )
            if (localView.findViewById<View>(volumeProgressId) is ProgressBar) {
                mDialogVolumeProgressBar =
                    localView.findViewById<View>(volumeProgressId) as ProgressBar
                if (mVolumeProgressDrawable != null && mDialogVolumeProgressBar != null) {
                    mDialogVolumeProgressBar.progressDrawable = mVolumeProgressDrawable
                }
            }
            mVolumeDialog = Dialog(activityContext, R.style.video_style_dialog_progress)
            mVolumeDialog.setContentView(localView)
            mVolumeDialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            mVolumeDialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            mVolumeDialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            mVolumeDialog.window!!.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val localLayoutParams = mVolumeDialog.window!!.attributes
            localLayoutParams.gravity = Gravity.TOP or Gravity.START
            localLayoutParams.width = width
            localLayoutParams.height = height
            val location = IntArray(2)
            getLocationOnScreen(location)
            localLayoutParams.x = location[0]
            localLayoutParams.y = location[1]
            mVolumeDialog.window!!.attributes = localLayoutParams
        }
        if (!mVolumeDialog.isShowing) {
            mVolumeDialog.show()
        }
        if (mDialogVolumeProgressBar != null) {
            mDialogVolumeProgressBar.progress = volumePercent
        }
    }

    /**
     * 显示亮度调节dialog 处理亮度调节计算后的数据
     */
    override fun showBrightnessDialog(percent: Float) {
        if (mBrightnessDialog == null) {
            val localView = LayoutInflater.from(activityContext).inflate(
                brightnessLayoutId, null
            )
            if (localView.findViewById<View>(R.id.brightness_progressbar) is ProgressBar) {
                mDialogBrightnessProgressBar =
                    localView.findViewById<View>(R.id.brightness_progressbar) as ProgressBar
            }
            mBrightnessDialog = Dialog(activityContext, R.style.video_style_dialog_progress)
            mBrightnessDialog.setContentView(localView)
            mBrightnessDialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            mBrightnessDialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            mBrightnessDialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            mBrightnessDialog.window!!.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val localLayoutParams = mBrightnessDialog.window!!.attributes
            localLayoutParams.gravity = Gravity.TOP or Gravity.START
            localLayoutParams.width = width
            localLayoutParams.height = height
            val location = IntArray(2)
            getLocationOnScreen(location)
            localLayoutParams.x = location[0]
            localLayoutParams.y = location[1]
            mBrightnessDialog.window!!.attributes = localLayoutParams
        }
        if (!mBrightnessDialog.isShowing) {
            mBrightnessDialog.show()
        }

        mDialogBrightnessProgressBar.progress = (percent * 100).toInt()
    }

    /**
     * 亮度调节计算
     */
    override fun onBrightnessSlide(percent: Float) {
        mBrightnessData = (mContext as Activity).window.attributes.screenBrightness
        if (mBrightnessData <= 0.00f) {
            mBrightnessData = 0.50f
        } else if (mBrightnessData < 0.01f) {
            mBrightnessData = 0.01f
        }
        val lpa = (mContext as Activity).window.attributes
        lpa.screenBrightness = mBrightnessData + percent
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f
        }
        showBrightnessDialog(lpa.screenBrightness)
        (mContext as Activity).window.attributes = lpa
    }

    /**
     * 计算滑动距离等 调用不同的处理逻辑
     */
    override fun touchSurfaceMove(deltaX: Float, deltaY: Float, y: Float) {
        var ownedDeltaY = deltaY
        var curWidth = 0
        var curHeight = 0
        if (activityContext != null) {
            curWidth =
                if (CommonUtil.getCurrentScreenLand(activityContext as Activity)) mScreenHeight else mScreenWidth
            curHeight =
                if (CommonUtil.getCurrentScreenLand(activityContext as Activity)) mScreenWidth else mScreenHeight
        }
        if (mChangePosition) {
            val totalTimeDuration = duration
            //当此反馈调整的时间为 上一次调整后的值
            val diffTimeMs = deltaX * totalTimeDuration / curWidth / mSeekRatio
            mSeekTimePosition =
                (mDownPosition + diffTimeMs).toInt()
                    .toLong()
            if (mSeekTimePosition < 0) {
                mSeekTimePosition = 0
            }
            if (mSeekTimePosition > totalTimeDuration) mSeekTimePosition = totalTimeDuration
            val seekTime = CommonUtil.stringForTime(mSeekTimePosition)

            val totalTime = CommonUtil.stringForTime(totalTimeDuration)

            val diffTimeNum = abs(diffTimeMs / 1000)
            val second = "%.1f".format(diffTimeNum.rem(60)).toDouble().toString()
            val minute = (diffTimeNum / 60).toInt().toString()
            val flag = if (diffTimeMs > 0) "+" else "-"
            val diffTime = "$flag$minute:$second"

            //同步更新 进度条左端的字符串
            if (mCurrentTimeTextView != null) {
                mCurrentTimeTextView.text = seekTime
            }

            showProgressDialogWithDiffTime(
                seekTime,
                diffTime,
                mSeekTimePosition,
                totalTime,
                totalTimeDuration
            )
        } else if (mChangeVolume) {
            ownedDeltaY = -ownedDeltaY
            val max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val deltaV = (max * ownedDeltaY * 3 / curHeight).toInt()
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0)
            val volumePercent =
                (mGestureDownVolume * 100 / max + ownedDeltaY * 3 * 100 / curHeight).toInt()
            showVolumeDialog(-ownedDeltaY, volumePercent)
        } else if (mBrightness) {
            //原本是滑动进度绝对值大于mThreshold ， 但是太麻烦 直接改了 还有个bug 当我调整亮度 出来之后 只要软件还开着会 一直拉回去 出了软件才会好 这个效果没有在我退出界面后销毁
            // 大概是我播放器没销毁的问题
            if (abs(ownedDeltaY) > 8) {
                val percent = -ownedDeltaY / curHeight
                onBrightnessSlide(percent)
                mDownY = y
            }
        }
    }

    /**
     * 显示播放进度dialog 处理进度信息
     */
    private fun showProgressDialogWithDiffTime(
        seekTime: String?,
        diffTime: String?,
        seekTimePosition: Long,
        totalTime: String,
        totalTimeDuration: Long,
    ) {
        if (mProgressDialog == null) {
            val localView = LayoutInflater.from(activityContext).inflate(
                progressDialogLayoutId, null
            )
            if (localView.findViewById<View>(progressDialogProgressId) is ProgressBar) {
                mDialogProgressBar =
                    localView.findViewById<View>(progressDialogProgressId) as ProgressBar
                if (mDialogProgressBarDrawable != null) {
                    mDialogProgressBar.progressDrawable = mDialogProgressBarDrawable
                }
            }
            if (localView.findViewById<View>(progressDialogCurrentDurationTextId) is TextView) {
                mDialogSeekTime = localView.findViewById<View>(
                    progressDialogCurrentDurationTextId
                ) as TextView
            }
            if (localView.findViewById<View>(progressDialogAllDurationTextId) is TextView) {
                mDialogTotalTime = localView.findViewById<View>(
                    progressDialogAllDurationTextId
                ) as TextView
            }
            if (localView.findViewById<View>(progressDialogImageId) is ImageView) {
                mDialogIcon = localView.findViewById<View>(progressDialogImageId) as ImageView
            }
            if (localView.findViewById<View>(R.id.tv_diff) is TextView) {
                mDialogDiffTime = localView.findViewById<View>(R.id.tv_diff) as TextView
            }
            mProgressDialog = Dialog(activityContext, R.style.video_style_dialog_progress)
            mProgressDialog.setContentView(localView)
            mProgressDialog.window!!.addFlags(Window.FEATURE_ACTION_BAR)
            mProgressDialog.window!!.addFlags(32)
            mProgressDialog.window!!.addFlags(16)
            mProgressDialog.window!!.setLayout(width, height)
            if (mDialogProgressNormalColor != -11 && mDialogTotalTime != null) {
                mDialogTotalTime.setTextColor(mDialogProgressNormalColor)
            }
            if (mDialogProgressHighLightColor != -11 && mDialogSeekTime != null) {
                mDialogSeekTime.setTextColor(mDialogProgressHighLightColor)
            }
            val localLayoutParams = mProgressDialog.window!!.attributes
            localLayoutParams.gravity = Gravity.TOP
            localLayoutParams.width = width
            localLayoutParams.height = height
            val location = IntArray(2)
            getLocationOnScreen(location)
            localLayoutParams.x = location[0]
            localLayoutParams.y = location[1]
            mProgressDialog.window!!.attributes = localLayoutParams
        }
        if (!mProgressDialog.isShowing) {
            mProgressDialog.show()
        }
        if (mDialogSeekTime != null) {
            mDialogSeekTime.text = seekTime
        }
        if (mDialogTotalTime != null) {
            mDialogTotalTime.text =
                String.format(resources.getString(R.string.total_time), totalTime)
        }
        if (totalTimeDuration > 0) if (mDialogProgressBar != null) {
            mDialogProgressBar.progress = (seekTimePosition * 100 / totalTimeDuration).toInt()
        }
        //获取当前进度 和调整后进度 获取调整进度
        //如何实现当前需求 因为当前的滑动的方法反馈是实时的 所以我需要一个定义在头部的可修改参数 每次点击按钮时 这个值应该要重置 重置的值为按下按钮时的进度
        //滑动时 diffTime的值为 修改的值加这个值
        mDialogDiffTime.text =
            String.format(resources.getString(R.string.diff_time), diffTime)
    }

    /**
     * 根据滑动类型更新滑动类型的标记
     */
    override fun touchSurfaceMoveFullLogic(absDeltaX: Float, absDeltaY: Float) {
        var curWidth = 0
        if (activityContext != null) {
            curWidth =
                if (CommonUtil.getCurrentScreenLand(activityContext as Activity)) mScreenHeight else mScreenWidth
        }

        //判断当前是否显示菜单 当显示菜单时 不处理滑动事件

        if (absDeltaX > mThreshold || absDeltaY > mThreshold) {
            cancelProgressTimer()
            if (absDeltaX >= mThreshold) {
                //防止全屏虚拟按键
                val screenWidth = CommonUtil.getScreenWidth(context)
                if (abs(screenWidth - mDownX) > mSeekEndOffset) {
                    //判断当前是否已经在修改
                    if (mChangePosition) {
                        //设置状态为暂停
                        if ((mCurrentState == CURRENT_STATE_PLAYING || (mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START && mVideoStateBackUp == CURRENT_STATE_PLAYING)) && mStopOnChangeProcess) onVideoPause()
                    }
                    mChangePosition = true

                    //这里更新视频状态为 暂停 在调整结束前隐藏 暂停和播放
                    //1.是否需要实现进度预览的功能
                    //2.是否需要将进度体现在进度条上？
                    //3.需要保存当前的状态 然后在取消时 将状态恢复

                    mDownPosition = currentPositionWhenPlaying

                    mStartButton.visibility = INVISIBLE
                } else {
                    mShowVKey = true
                }
            } else {
                val screenHeight = CommonUtil.getScreenHeight(context)
                val noEnd = abs(screenHeight - mDownY) > mSeekEndOffset
                if (mFirstTouch) {
                    mBrightness = mDownX < curWidth * 0.5f && noEnd
                    mFirstTouch = false
                }
                if (!mBrightness) {
                    mChangeVolume = noEnd
                    mGestureDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                }
                mShowVKey = !noEnd
            }
        }
    }

    /**
     * 视频的回调 代表当前状态的变更
     */
    override fun onInfo(what: Int, extra: Int) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            mVideoStateBackUp = mCurrentState
            //避免在onPrepared之前就进入了buffering，导致一直loading
            if (mHadPlay && mCurrentState != CURRENT_STATE_PREPAREING && mCurrentState > 0) setStateAndUi(
                CURRENT_STATE_PLAYING_BUFFERING_START
            )

        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            if (mVideoStateBackUp != -1) {
                if (mVideoStateBackUp == CURRENT_STATE_PLAYING_BUFFERING_START) {
                    mVideoStateBackUp = CURRENT_STATE_PLAYING
                }
                if (mHadPlay && (mCurrentState != CURRENT_STATE_PREPAREING) && mCurrentState > 0) setStateAndUi(
                    mVideoStateBackUp
                )
                //判断当前是否正在修改进度 如果正在修改进度 不修改这个值
                if (!mChangePosition) {
                    mVideoStateBackUp = -1
                }

            }
        } else if (what == gsyVideoManager.rotateInfoFlag) {
            mRotate = extra
            Debuger.printfLog("Video Rotate Info $extra")
            if (mTextureView != null) mTextureView.rotation = mRotate.toFloat()
        }
    }

    /**
     * 设置当前的ui显示状态 但是不修改此时的视频显示状态
     */
    override fun setStateAndUi(state: Int) {
        mCurrentState = state
        if (state == CURRENT_STATE_NORMAL && isCurrentMediaListener || state == CURRENT_STATE_AUTO_COMPLETE || state == CURRENT_STATE_ERROR) {
            mHadPrepared = false
        }
        when (mCurrentState) {
            CURRENT_STATE_NORMAL -> {
                if (isCurrentMediaListener) {
                    Debuger.printfLog("${this.hashCode()}------------------------------ dismiss CURRENT_STATE_NORMAL")
                    cancelProgressTimer()
                    gsyVideoManager.releaseMediaPlayer()
                    releasePauseCover()
                    mBufferPoint = 0
                    mSaveChangeViewTIme = 0
                    if (mAudioManager != null) {
                        //mAudioManager.abandonAudioFocus(onAudioFocusChangeListener)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val audioFocusRequest =
                                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                                    .setOnAudioFocusChangeListener(onAudioFocusChangeListener)
                                    .build()
                            mAudioManager.abandonAudioFocusRequest(audioFocusRequest)
                        } else {
                            @Suppress("DEPRECATION")
                            mAudioManager.abandonAudioFocus(onAudioFocusChangeListener)
                        }
                    }
                }
                releaseNetWorkState()
            }

            CURRENT_STATE_PREPAREING -> resetProgressAndTime()
            CURRENT_STATE_PLAYING -> if (isCurrentMediaListener) {
                Debuger.printfLog("${this.hashCode()}------------------------------ CURRENT_STATE_PLAYING")
                startProgressTimer()
            }

            CURRENT_STATE_PAUSE -> {
                Debuger.printfLog("${this.hashCode()}------------------------------ CURRENT_STATE_PAUSE")
                startProgressTimer()
            }

            CURRENT_STATE_ERROR -> if (isCurrentMediaListener) {
                gsyVideoManager.releaseMediaPlayer()
            }

            CURRENT_STATE_AUTO_COMPLETE -> {
                Debuger.printfLog("${this.hashCode()}------------------------------ dismiss CURRENT_STATE_AUTO_COMPLETE")
                cancelProgressTimer()
                if (mProgressBar != null) {
                    mProgressBar.progress = 100
                }
                if (mCurrentTimeTextView != null && mTotalTimeTextView != null) {
                    mCurrentTimeTextView.text = mTotalTimeTextView.text
                }
                if (mBottomProgressBar != null) {
                    mBottomProgressBar.progress = 100
                }
            }
        }
        resolveUIState(state)
        if (mGsyStateUiListener != null) {
            mGsyStateUiListener.onStateChanged(state)
        }
    }


    /**
     * 获取当前的播放进度
     */
    override fun getCurrentPositionWhenPlaying(): Long {
        var position: Long = 0
        if (mCurrentState == CURRENT_STATE_PLAYING || mCurrentState == CURRENT_STATE_PAUSE || mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START) {
            position = try {
                gsyVideoManager.currentPosition
            } catch (e: Exception) {
                e.printStackTrace()
                return 0
            }
        }
        return if (position == 0L && mCurrentPosition > 0) {
            mCurrentPosition
        } else position
    }

    /**
     * 单击主界面
     */
    override fun touchSurfaceDown(x: Float, y: Float) {
        mTouchingProgressBar = true
        mDownX = x
        mDownY = y
        mMoveY = 0f
        mChangeVolume = false
        mChangePosition = false
        mShowVKey = false
        mBrightness = false
        mFirstTouch = true

        //记录当前的状态 该值只在移动进度条时存储 当当前状态为正在加载 不修改此值
        if (mCurrentState != CURRENT_STATE_PLAYING_BUFFERING_START) {
            mVideoStateBackUp = mCurrentState
        }

    }

    /**
     * 手指 离开屏幕处理
     */
    override fun touchSurfaceUp() {
        if (mChangePosition) {
            val duration = duration
            val progress = mSeekTimePosition * 100 / if (duration == 0L) 1 else duration
            if (mBottomProgressBar != null) mBottomProgressBar.progress = progress.toInt()
        }
        mTouchingProgressBar = false
        dismissProgressDialog()
        dismissVolumeDialog()
        dismissBrightnessDialog()
        if (mChangePosition && (mCurrentState == CURRENT_STATE_PLAYING || mCurrentState == CURRENT_STATE_PAUSE || mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START)) {

            //恢复视频状态
            if (mVideoStateBackUp == CURRENT_STATE_PLAYING) {
                videoPlayUnSetUi()
            } else if (mVideoStateBackUp == CURRENT_STATE_PAUSE) {
                videoPauseUnSetUi()
            }

            //将是否正在修改进度改为false
            mChangePosition = false
            try {
                //如果是 m3u8 可能会需要这个
                //if(mSeekTimePosition == 0) {
                //    mSeekTimePosition = 1;
                //}

                gsyVideoManager.seekTo(mSeekTimePosition)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            val duration = duration
            val progress = mSeekTimePosition * 100 / if (duration == 0L) 1 else duration
            if (mProgressBar != null) {
                mProgressBar.progress = progress.toInt()
            }
            if (mVideoAllCallBack != null && isCurrentMediaListener) {
                Debuger.printfLog("onTouchScreenSeekPosition")
                mVideoAllCallBack.onTouchScreenSeekPosition(mOriginUrl, mTitle, this)
            }


        } else if (mBrightness) {
            if (mVideoAllCallBack != null && isCurrentMediaListener) {
                Debuger.printfLog("onTouchScreenSeekLight")
                mVideoAllCallBack.onTouchScreenSeekLight(mOriginUrl, mTitle, this)
            }
        } else if (mChangeVolume) {
            if (mVideoAllCallBack != null && isCurrentMediaListener) {
                Debuger.printfLog("onTouchScreenSeekVolume")
                mVideoAllCallBack.onTouchScreenSeekVolume(mOriginUrl, mTitle, this)
            }
        }
    }

    /**
     * 触摸滑动业务逻辑
     */
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val id = v.id
        val x = event.x
        val y = event.y
        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
            onClickUiToggle(event)
            startDismissControlViewTimer()
            return true
        }
        if (id == R.id.fullscreen) {
            return false
        }
        if (id == R.id.surface_container) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> touchSurfaceDown(x, y)
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = x - mDownX
                    val deltaY = y - mDownY
                    val absDeltaX = abs(deltaX)
                    val absDeltaY = abs(deltaY)

                    //判断 xy 的位置是否为屏幕顶部的 5/1
                    //如果是 且所有标记为false 则不处理此次滑动事件
                    if (y < mScreenHeight / 7 && !mChangePosition && !mChangeVolume && !mBrightness) {
                        return false
                    }

                    if (mIfCurrentIsFullscreen && mIsTouchWigetFull || mIsTouchWiget && !mIfCurrentIsFullscreen) {
                        if (!mChangePosition && !mChangeVolume && !mBrightness) {
                            touchSurfaceMoveFullLogic(absDeltaX, absDeltaY)
                        }
                    }

                    touchSurfaceMove(deltaX, deltaY, y)
                }

                MotionEvent.ACTION_UP -> {
                    startDismissControlViewTimer()
                    touchSurfaceUp()
                    Debuger.printfLog(
                        "${this.hashCode()}------------------------------ surface_container ACTION_UP"
                    )
                    startProgressTimer()

                    //手指抬起后 将所有标记置为false 防止出现上一次判断影响到这一次的
                    mChangePosition = false
                    mChangeVolume = false
                    mBrightness = false

                    //不要和隐藏虚拟按键后，滑出虚拟按键冲突
                    if (mHideKey && mShowVKey) {
                        return true
                    }
                }
            }
            gestureDetector.onTouchEvent(event)
        } else if (id == R.id.progress) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    cancelDismissControlViewTimer()
                    cancelProgressTimer()
                    var vpDown = parent
                    while (vpDown != null) {
                        vpDown.requestDisallowInterceptTouchEvent(true)
                        vpDown = vpDown.parent
                    }
                    sProgressBarStartCurrentPosition = currentPositionWhenPlaying
                }

                //移动进度条 显示 dialog
                MotionEvent.ACTION_MOVE -> {
                    cancelProgressTimer()
                    var vpDown = parent
                    while (vpDown != null) {
                        vpDown.requestDisallowInterceptTouchEvent(true)
                        vpDown = vpDown.parent
                    }

                    //总时间
                    val totalTimeDuration = duration
                    val totalTime = CommonUtil.stringForTime(totalTimeDuration)
                    //结束时间
                    val endPosition: Long = mProgressBar.progress * duration / 100
                    //结束时间字符串
                    val seekTime =
                        CommonUtil.stringForTime(mProgressBar.progress * duration / 100)

                    //同步更新 进度条左端的字符串
                    if (mCurrentTimeTextView != null) {
                        mCurrentTimeTextView.text = seekTime
                    }

                    //仅在全屏生效
                    if (mIfCurrentIsFullscreen) {
                        //开始结束区间时间
                        val diffTimeMs = endPosition - sProgressBarStartCurrentPosition
                        val diffTimeNum = abs(diffTimeMs / 1000)
                        val second1 = diffTimeNum.rem(60)
                        val second = "%.1f".format(second1.toDouble())
                        val minute = (diffTimeNum / 60).toInt().toString()
                        val flag = if (diffTimeMs > 0) "+" else "-"
                        val diffTime = "$flag$minute:$second"

                        mSeekTimePosition = (sProgressBarStartCurrentPosition + diffTimeMs)

                        showProgressDialogWithDiffTime(
                            seekTime,
                            diffTime,
                            mSeekTimePosition,
                            totalTime,
                            totalTimeDuration
                        )

                    }

                }

                MotionEvent.ACTION_UP -> {
                    startDismissControlViewTimer()
                    Debuger.printfLog(
                        "${this.hashCode()}----------------------------- progress ACTION_UP"
                    )
                    startProgressTimer()
                    var vPup = parent
                    while (vPup != null) {
                        vPup.requestDisallowInterceptTouchEvent(false)
                        vPup = vPup.parent
                    }
                    mBrightnessData = -1f

                    if (mProgressDialog != null) {
                        if (mProgressDialog.isShowing) {
                            mProgressDialog.dismiss()
                        }
                    }


                }
            }
        }
        return false
    }

    override fun touchDoubleUp(e: MotionEvent) {
        if (!mHadPlay) {
            return
        }
        //双击事件处理 分三个区域 中心区域为 暂停 左右区域为后退5秒前进5秒
        val curWidth =
            if (CommonUtil.getCurrentScreenLand(context as Activity)) mScreenHeight else mScreenWidth


        //判断当前点击位置
        if (e.x > curWidth * 2 / 3) {
            //阻止菜单等界面的显示
            //前进10秒
            startDismissControlViewTimer()
            if (mCurrentState == CURRENT_STATE_PLAYING || mCurrentState == CURRENT_STATE_PAUSE) {
                seekTo(currentPositionWhenPlaying + 10000)
            }

        } else if (e.x < curWidth / 3) {
            //阻止菜单等界面的显示
            startDismissControlViewTimer()
            //后退10秒
            if (mCurrentState == CURRENT_STATE_PLAYING || mCurrentState == CURRENT_STATE_PAUSE) {
                seekTo(currentPositionWhenPlaying - 10000)
            }
        } else {
            //暂停
            clickStartIcon()
        }
    }

    /**
     * 获取音量dialog
     */
    override fun getVolumeLayoutId(): Int {
        return R.layout.video_volume_dialog
    }

    /**
     * 获取音量进度条对象
     */
    override fun getVolumeProgressId(): Int {
        return R.id.volume_progressbar
    }

    /**
     * 获取亮度dialog
     */
    override fun getBrightnessLayoutId(): Int {
        return R.layout.video_brightness_dialog
    }

    /**
     * 获取拖动进度dialog对象
     */
    override fun getProgressDialogLayoutId(): Int {
        return R.layout.video_progress_dialog_diff
    }


    override fun changeUiToPreparingShow() {
        Debuger.printfLog("changeUiToPreparingShow")

        setViewShowState(mTopContainer, INVISIBLE)
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
        setViewShowState(mLoadingProgressBar, VISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(mLockScreen, GONE)

        if (mLoadingProgressBar is ENDownloadView) {
            val enDownloadView = mLoadingProgressBar as ENDownloadView
            if (enDownloadView.currentState == ENDownloadView.STATE_PRE) {
                (mLoadingProgressBar as ENDownloadView).start()
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioFocusRequest =
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setOnAudioFocusChangeListener(onAudioFocusChangeListener).build()
            mAudioManager.requestAudioFocus(audioFocusRequest)
        } else {
            @Suppress("DEPRECATION")
            mAudioManager.requestAudioFocus(
                onAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            )
        }

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

    @UnstableApi
    override fun onClick(v: View) {
        when (v.id) {
            R.id.fullscreen -> {
                /**
                 * 确定出现错误的位置
                 * orientationUtils.resolveByClick() 没有成功的把屏幕旋转过来
                 */
                orientationUtils.resolveByClick()
                startWindowFullscreen(context, actionBar = true, statusBar = true)
            }

            R.id.clarity -> {
                showDialog(mClarityDialog)
                //查看 GSYVideoControlView 1097行 1026行 829行 需要添加状态 设置页出现 重写 resolveUIState
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

            R.id.lottie_start -> {
                println(mLottieStart.frame)
                if (mLottieStart.frame > 0) {
                    playStopLottieView()
                } else {
                    playStartLottieView()
                }

            }
        }

        super.onClick(v)
    }

    private fun playStartLottieView() {
        releaseAnimatorUpdateListener()
        valueAnimator = ValueAnimator.ofFloat(0f, 1.0f)
        animatorUpdateListener =
            ValueAnimator.AnimatorUpdateListener { animation ->
                mLottieStart.progress = animation.animatedValue as Float
            }
        valueAnimator.addUpdateListener(animatorUpdateListener)
        valueAnimator.start()
    }

    private fun playStopLottieView() {
        releaseAnimatorUpdateListener()
        valueAnimator = ValueAnimator.ofFloat(1f, 0f)
        animatorUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
            mLottieStart.progress =
                animation.animatedValue as Float
        }
        valueAnimator.addUpdateListener(animatorUpdateListener)
        valueAnimator.start()
    }

    private fun releaseAnimatorUpdateListener() {
        valueAnimator.removeUpdateListener(animatorUpdateListener)
    }

    private fun videoPauseUnSetUi() {
        try {
            onVideoPause()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (mVideoAllCallBack != null && isCurrentMediaListener) {
            if (mIfCurrentIsFullscreen) {
                Debuger.printfLog("onClickStopFullscreen")
                mVideoAllCallBack.onClickStopFullscreen(mOriginUrl, mTitle, this)
            } else {
                Debuger.printfLog("onClickStop")
                mVideoAllCallBack.onClickStop(mOriginUrl, mTitle, this)
            }
        }
    }

    private fun videoPause() {
        try {
            onVideoPause()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        setStateAndUi(CURRENT_STATE_PAUSE)
        if (mVideoAllCallBack != null && isCurrentMediaListener) {
            if (mIfCurrentIsFullscreen) {
                Debuger.printfLog("onClickStopFullscreen")
                mVideoAllCallBack.onClickStopFullscreen(mOriginUrl, mTitle, this)
            } else {
                Debuger.printfLog("onClickStop")
                mVideoAllCallBack.onClickStop(mOriginUrl, mTitle, this)
            }
        }
    }

    private fun videoPlayUnSetUi() {
        if (mVideoAllCallBack != null && isCurrentMediaListener) {
            if (mIfCurrentIsFullscreen) {
                Debuger.printfLog("onClickResumeFullscreen")
                mVideoAllCallBack.onClickResumeFullscreen(mOriginUrl, mTitle, this)
            } else {
                Debuger.printfLog("onClickResume")
                mVideoAllCallBack.onClickResume(mOriginUrl, mTitle, this)
            }
        }
        if (!mHadPlay && !mStartAfterPrepared) {
            startAfterPrepared()
        }
        try {
            gsyVideoManager.start()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun videoPlay() {
        videoPlayUnSetUi()
        setStateAndUi(CURRENT_STATE_PLAYING)
    }


    override fun clickStartIcon() {
        if (TextUtils.isEmpty(mUrl)) {
            Debuger.printfError("********" + resources.getString(R.string.no_url))
            //Toast.makeText(getActivityContext(), getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
            return
        }
        when (mCurrentState) {
            CURRENT_STATE_NORMAL, CURRENT_STATE_ERROR -> {
                if (isShowNetConfirm) {
                    showWifiDialog()
                    return
                }
                startButtonLogic()
            }

            CURRENT_STATE_PLAYING -> {
                videoPause()
            }

            CURRENT_STATE_PAUSE -> {
                videoPlay()
            }

            CURRENT_STATE_AUTO_COMPLETE -> {
                startButtonLogic()
            }
        }
    }


    /**
     * 显示对话弹出框后 控件一直存在
     */
    private fun showDialog(v: ViewGroup) {
        mDialog.visibility = VISIBLE
        v.visibility = VISIBLE
        val animator = AnimatorInflater.loadAnimator(context, R.animator.dialog_in)
        animator.setTarget(v)
        animator.start()
    }

    /**
     * 显示对话弹出框后 控件消失
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

    @UnstableApi
    override fun onCues(cueGroup: CueGroup) {
        mSubtitleView.setCues(cueGroup.cues)
    }

    /**********以下重载 GSYVideoPlayer 的 全屏 SubtitleView 相关实现 */
    @UnstableApi
    override fun startWindowFullscreen(
        context: Context,
        actionBar: Boolean,
        statusBar: Boolean,
    ): GSYBaseVideoPlayer {
        val gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar)
        val gsyExoSubTitleVideoView = gsyBaseVideoPlayer as SmovVideoView

        (GSYExoSubTitleVideoManager.instance().player as GSYExoSubTitlePlayerManager).addTextOutputPlaying(
            gsyExoSubTitleVideoView
        )

        gsyBaseVideoPlayer.setSubtitleViewTextSize(24f)

        gsyBaseVideoPlayer.mLockScreen.visibility = VISIBLE

        gsyBaseVideoPlayer.mBottomFunction.visibility = VISIBLE
        gsyBaseVideoPlayer.mBottomContainer.setPadding(20, 0, 10, 0)

        return gsyBaseVideoPlayer
    }

    @UnstableApi
    override fun resolveNormalVideoShow(
        oldF: View,
        vp: ViewGroup,
        gsyVideoPlayer: GSYVideoPlayer,
    ) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer)

        val gsyExoSubTitleVideoView = gsyVideoPlayer as SmovVideoView
        (GSYExoSubTitleVideoManager.instance().player as GSYExoSubTitlePlayerManager).removeTextOutput(
            gsyExoSubTitleVideoView
        )

        gsyExoSubTitleVideoView.setSubtitleViewTextSize(14f)

        //加了这个横竖屏旋转正常了。。。
        orientationUtils.resolveByClick()
        gsyExoSubTitleVideoView.mBottomFunction.visibility = GONE
        gsyExoSubTitleVideoView.mBottomContainer.setPadding(5, 0, 5, 0)
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

    @UnstableApi
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

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        super.onProgressChanged(seekBar, progress, fromUser)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        super.onStartTrackingTouch(seekBar)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        super.onStopTrackingTouch(seekBar)
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

