package com.leri.smovbook.ui.player

import android.app.Activity
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import cn.jzvd.JzvdStd
import kotlinx.parcelize.Parcelize

@Parcelize
data class SmovBookVideoState(
    val state: Int = -1,
    val screen: Int = -1,
    val widthRatio: Int = 0,
    val heightRatio: Int = 0,
    val mediaInterfaceClass: Class<*>? = null,
    var positionInList: Int = -1,
    var videoRotation: Int = 0,
    var seekToManulPosition: Int = -1,
    var seekToInAdvance: Long = 0,
    var preloading: Boolean = false,
) : Parcelable


@Composable
fun rememberVideoPlayer(
    url: String? = null,
    title: String? = null
): SmovBookVideoActive {
    val context = LocalContext.current as Activity

    val coroutineScope = rememberCoroutineScope()

    return rememberSaveable(
        context, coroutineScope,
        saver = object : Saver<SmovBookVideoActive, SmovBookVideoState> {
            override fun restore(value: SmovBookVideoState): SmovBookVideoActive {
                return SmovBookVideoActive(
                    context = context
                ).apply {
                    state = value.state
                    screen = value.screen
                    widthRatio = value.widthRatio
                    heightRatio = value.heightRatio
                    mediaInterfaceClass = value.mediaInterfaceClass
                    positionInList = value.positionInList
                    videoRotation = value.videoRotation
                    seekToManulPosition = value.seekToManulPosition
                    seekToInAdvance = value.seekToInAdvance
                    preloading = value.preloading
                }
            }

            override fun SaverScope.save(value: SmovBookVideoActive): SmovBookVideoState {
                return value.currentState { it }
            }
        },
        init = {
            SmovBookVideoActive(
                context = context,
            ).apply {
                setUp(
                    url,
                    title,
                    JzvdStd.SCREEN_NORMAL
                )
            }
        }
    )
}