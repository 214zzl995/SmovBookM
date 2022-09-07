package com.leri.smovbook.ui

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.leri.smovbook.ui.home.LocalBackPressedDispatcher
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalAnimationApi
class MainActivity : AppCompatActivity() {

    companion object {
        const val splashFadeDurationMillis = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val splashWasDisplayed = savedInstanceState != null
        if (!splashWasDisplayed) {
            val splashScreen = installSplashScreen()


            splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
                // Get icon instance and start a fade out animation
                splashScreenViewProvider.iconView
                    .animate()
                    .setDuration(splashFadeDurationMillis.toLong())
                    .alpha(0f)
                    .withEndAction {
                        // After the fade out, remove the splash and set content view
                        splashScreenViewProvider.remove()
                        setContent {
                            val decorView: View = window.decorView
                            CompositionLocalProvider(
                                LocalBackPressedDispatcher provides this.onBackPressedDispatcher
                            ) {
                                SmovApp(modifier = Modifier.autoCloseKeyboard(decorView))
                            }
                        }
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


    }

    @Suppress("DEPRECATION")
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //android11的新api
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val decorView: View = window.decorView
            val windowInsetsController = decorView.windowInsetsController
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                windowInsetsController?.show(WindowInsets.Type.statusBars())
            } else {
                windowInsetsController?.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                windowInsetsController?.hide(WindowInsets.Type.statusBars())
            }

        } else {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                val decorView: View = window.decorView
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            } else {
                val decorView: View = window.decorView;
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }
    }
}
