package com.leri.smovbook.ui

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.leri.smovbook.ui.home.LocalBackPressedDispatcher
import com.leri.smovbook.ui.player.exosubtitle.GSYExoSubTitleVideoManager
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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
            runBlocking {
                delay(splashFadeDurationMillis.toLong())
            }
            setContent {
                val decorView: View = window.decorView
                CompositionLocalProvider(
                    LocalBackPressedDispatcher provides this.onBackPressedDispatcher
                ) {
                    SmovApp(modifier = Modifier.autoCloseKeyboard(decorView))
                }
            }
            return@setKeepOnScreenCondition false
        }
    }

    override fun onPause() {
        super.onPause()
        /// ???????????????
        GSYVideoType.enableMediaCodec()
        GSYExoSubTitleVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        GSYExoSubTitleVideoManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        /// ???????????????
        GSYVideoType.disableMediaCodec()
        GSYExoSubTitleVideoManager.releaseAllVideos()
    }
}
