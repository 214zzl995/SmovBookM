package com.leri.smovbook.ui

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.leri.smovbook.ui.home.LocalBackPressedDispatcher
import com.leri.smovbook.ui.player.exosubtitle.GSYExoSubTitleVideoManager
import com.leri.smovbook.ui.util.autoCloseKeyboard
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    companion object {
        const val splashFadeDurationMillis = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val splashWasDisplayed = savedInstanceState != null
        if (!splashWasDisplayed) {
            splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
                splashScreenViewProvider.iconView
                    .animate()
                    .setDuration(splashFadeDurationMillis.toLong())
                    .alpha(0f)
                    .withEndAction {
                        splashScreenViewProvider.remove()
                    }.start()
            }
        } else {
            setContent {
                val decorView: View = window.decorView
                CompositionLocalProvider(
                    LocalBackPressedDispatcher provides this.onBackPressedDispatcher
                ) {
                    SmovApp(modifier = Modifier.autoCloseKeyboard(decorView))
                }
            }
        }

        splashScreen.setKeepOnScreenCondition {
            setContent {
                val decorView: View = window.decorView
                CompositionLocalProvider(
                    LocalBackPressedDispatcher provides this.onBackPressedDispatcher
                ) {
                    CompositionLocalProvider(LocalOkHttpClient provides okHttpClient) {
                        SmovApp(modifier = Modifier.autoCloseKeyboard(decorView))
                    }
                }
            }
            return@setKeepOnScreenCondition false
        }
    }

    override fun onPause() {
        super.onPause()
        /// 打开硬解码
        GSYVideoType.enableMediaCodec()
        GSYExoSubTitleVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        GSYExoSubTitleVideoManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        /// 关闭硬解码
        GSYVideoType.disableMediaCodec()
        GSYExoSubTitleVideoManager.releaseAllVideos()
    }
}

val LocalOkHttpClient = staticCompositionLocalOf { OkHttpClient.Builder().build() }
