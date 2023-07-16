package com.leri.smovbook.ui.player.utils

import kotlin.jvm.JvmOverloads
import android.app.Activity
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.utils.OrientationOption
import android.view.OrientationEventListener
import android.content.pm.ActivityInfo
import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import android.view.Surface
import androidx.annotation.RequiresApi
import com.shuyu.gsyvideoplayer.utils.Debuger
import java.lang.IllegalStateException
import java.lang.ref.WeakReference

/**
 * 处理屏幕旋转的的逻辑
 * Created by leri on 2022/09/23.
 */
@RequiresApi(Build.VERSION_CODES.R)
class OrientationUtils @JvmOverloads constructor(
    activity: Activity,
    gsyVideoPlayer: GSYBaseVideoPlayer?,
    orientationOption: OrientationOption? = null
) {
    private val mActivity: WeakReference<Activity?>
    private val mVideoPlayer: GSYBaseVideoPlayer?
    private lateinit var mOrientationEventListener: OrientationEventListener
    var orientationOption: OrientationOption? = null
    var screenType = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    var isLand = LAND_TYPE_NULL
    var isClick = false
    var isClickLand = false
    var isClickPort = false
    private var mEnable = true

    //是否跟随系统
    var isRotateWithSystem = true
    var isPause = false

    /**
     * 旋转时仅处理横屏
     */
    var isOnlyRotateLand = false


    init {
        mActivity = WeakReference<Activity?>(activity)
        mVideoPlayer = gsyVideoPlayer
        if (orientationOption == null) {
            this.orientationOption = OrientationOption()
        } else {
            this.orientationOption = orientationOption
        }
        initGravity(activity)
        init()
    }

    private fun init() {
        val activity = mActivity.get() ?: return
        val context = activity.applicationContext
        mOrientationEventListener = object : OrientationEventListener(context) {
            @SuppressLint("SourceLockedOrientationActivity")
            override fun onOrientationChanged(rotation: Int) {
                val autoRotateOn = Settings.System.getInt(
                    context.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0
                ) == 1
                if (!autoRotateOn && isRotateWithSystem) {
                    if (!isOnlyRotateLand || isLand == LAND_TYPE_NULL) {
                        return
                    }
                }
                if (mVideoPlayer != null && mVideoPlayer.isVerticalFullByVideoSize) {
                    return
                }
                if (isPause) {
                    return
                }
                // 设置竖屏
                if (rotation >= 0 && rotation <= orientationOption!!.normalPortraitAngleStart || rotation >= orientationOption!!.normalPortraitAngleEnd) {
                    if (isClick) {
                        if (isLand > LAND_TYPE_NULL && !isClickLand) {
                            return
                        } else {
                            isClickPort = true
                            isClick = false
                            isLand = LAND_TYPE_NULL
                        }
                    } else {
                        if (isLand > LAND_TYPE_NULL) {
                            if (!isOnlyRotateLand) {
                                screenType = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                if (mVideoPlayer!!.fullscreenButton != null) {
                                    if (mVideoPlayer.isIfCurrentIsFullscreen) {
                                        mVideoPlayer.fullscreenButton.setImageResource(
                                            mVideoPlayer.shrinkImageRes
                                        )
                                    } else {
                                        mVideoPlayer.fullscreenButton.setImageResource(
                                            mVideoPlayer.enlargeImageRes
                                        )
                                    }
                                }
                                isLand = LAND_TYPE_NULL
                            }
                            isClick = false
                        }
                    }
                } else if (rotation >= orientationOption!!.normalLandAngleStart && rotation <= orientationOption!!.normalLandAngleEnd) {
                    if (isClick) {
                        if (isLand != LAND_TYPE_NORMAL && !isClickPort) {
                            return
                        } else {
                            isClickLand = true
                            isClick = false
                            isLand = LAND_TYPE_NORMAL
                        }
                    } else {
                        if (isLand != LAND_TYPE_NORMAL) {
                            screenType = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                            if (mVideoPlayer!!.fullscreenButton != null) {
                                mVideoPlayer.fullscreenButton.setImageResource(mVideoPlayer.shrinkImageRes)
                            }
                            isLand = 1
                            isClick = false
                        }
                    }
                } else if (rotation > orientationOption!!.reverseLandAngleStart && rotation < orientationOption!!.reverseLandAngleEnd) {
                    if (isClick) {
                        if (isLand != LAND_TYPE_REVERSE && !isClickPort) {
                            return
                        } else {
                            isClickLand = true
                            isClick = false
                            isLand = LAND_TYPE_REVERSE
                        }
                    } else if (isLand != LAND_TYPE_REVERSE) {
                        screenType = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
                        if (mVideoPlayer!!.fullscreenButton != null) {
                            mVideoPlayer.fullscreenButton.setImageResource(mVideoPlayer.shrinkImageRes)
                        }
                        isLand = LAND_TYPE_REVERSE
                        isClick = false
                    }
                }
            }
        }
        mOrientationEventListener.enable()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initGravity(activity: Activity) {
        if (isLand == LAND_TYPE_NULL) {
            when (activity.display!!.rotation) {
                Surface.ROTATION_0 -> {
                    // 竖向为正方向。 如：手机、小米平板
                    isLand = LAND_TYPE_NULL
                    screenType = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                Surface.ROTATION_270 -> {
                    // 横向为正方向。 如：三星、sony平板
                    isLand = LAND_TYPE_REVERSE
                    screenType = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                }
                else -> {
                    // 未知方向
                    isLand = LAND_TYPE_NORMAL
                    screenType = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setRequestedOrientation(requestedOrientation: Int) {
        val activity = mActivity.get() ?: return
        try {
            activity.requestedOrientation = requestedOrientation
        } catch (exception: IllegalStateException) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O || Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) {
                Debuger.printfError("OrientationUtils", exception)
            } else {
                exception.printStackTrace()
            }
        }
    }

    /**
     * 点击切换的逻辑，比如竖屏的时候点击了就是切换到横屏不会受屏幕的影响
     *
     * SCREEN_ORIENTATION_UNSPECIFIED 方向未指定
     * SCREEN_ORIENTATION_LANDSCAPE 方向横屏
     * SCREEN_ORIENTATION_PORTRAIT 方向竖屏
     * SCREEN_ORIENTATION_USER 方向用户
     * SCREEN_ORIENTATION_BEHIND 方向背后
     * SCREEN_ORIENTATION_SENSOR 方向传感器
     * SCREEN_ORIENTATION_NOSENSOR 方向没有传感器
     * SCREEN_ORIENTATION_SENSOR_LANDSCAPE 传感器横向
     * SCREEN_ORIENTATION_SENSOR_PORTRAIT 传感器竖向
     * SCREEN_ORIENTATION_REVERSE_LANDSCAPE 反转横向
     * SCREEN_ORIENTATION_REVERSE_PORTRAIT 反转竖向
     * SCREEN_ORIENTATION_FULL_SENSOR 全传感器
     * SCREEN_ORIENTATION_USER_LANDSCAPE 用户横向
     * SCREEN_ORIENTATION_USER_PORTRAIT 用户竖向
     * SCREEN_ORIENTATION_FULL_USER 方向用户
     * SCREEN_ORIENTATION_LOCKED 方向锁定
     *
     * ！！！如果不需要旋转屏幕，可以不调用！！！
     */
    @SuppressLint("SourceLockedOrientationActivity")
    fun resolveByClick() {
        println("检查")
        if (isLand == LAND_TYPE_NULL && mVideoPlayer != null && mVideoPlayer.isVerticalFullByVideoSize) {
            return
        }
        isClick = true
        val activity = mActivity.get() ?: return

        if (isLand == LAND_TYPE_NULL) {
            val request = activity.requestedOrientation

            //需要增加 是否锁定的判断 锁定情况下直接锁定
            screenType = if (request == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
            setRequestedOrientation(screenType)
            if (mVideoPlayer!!.fullscreenButton != null) {
                mVideoPlayer.fullscreenButton.setImageResource(mVideoPlayer.shrinkImageRes)
            }
            isLand = LAND_TYPE_NORMAL
            isClickLand = false
        } else {
            screenType = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            if (mVideoPlayer!!.fullscreenButton != null) {
                if (mVideoPlayer.isIfCurrentIsFullscreen) {
                    mVideoPlayer.fullscreenButton.setImageResource(mVideoPlayer.shrinkImageRes)
                } else {
                    mVideoPlayer.fullscreenButton.setImageResource(mVideoPlayer.enlargeImageRes)
                }
            }
            isLand = LAND_TYPE_NULL
            isClickPort = false
        }
    }

    /**
     * 列表返回的样式判断。因为立即旋转会导致界面跳动的问题
     */
    @SuppressLint("SourceLockedOrientationActivity")
    fun backToProtVideo(): Int {
        if (isLand > LAND_TYPE_NULL) {
            isClick = true
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            if (mVideoPlayer != null && mVideoPlayer.fullscreenButton != null) mVideoPlayer.fullscreenButton.setImageResource(
                mVideoPlayer.enlargeImageRes
            )
            isLand = LAND_TYPE_NULL
            isClickPort = false
            return 500
        }
        return LAND_TYPE_NULL
    }

    var isEnable: Boolean
        get() = mEnable
        set(enable) {
            mEnable = enable
            if (mEnable) {
                mOrientationEventListener.enable()
            } else {
                mOrientationEventListener.disable()
            }
        }

    fun releaseListener() {
        mOrientationEventListener.disable()
    }

    companion object {
        private const val LAND_TYPE_NULL = 0
        private const val LAND_TYPE_NORMAL = 1
        private const val LAND_TYPE_REVERSE = 2
    }
}