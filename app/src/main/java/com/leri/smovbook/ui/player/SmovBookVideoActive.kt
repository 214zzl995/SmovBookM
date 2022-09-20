package com.leri.smovbook.ui.player

import android.content.Context
import android.util.AttributeSet
import cn.jzvd.JZDataSource
import cn.jzvd.JzvdStd
import com.leri.smovbook.ui.player.SmovBookVideoState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Created by Leri
 * On 2022/09/19 22:24
 */
class SmovBookVideoActive : JzvdStd {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)


    lateinit var changeFullscreen: () -> Unit

    private val _state = MutableStateFlow(SmovBookVideoState(state = 1))

    val state: StateFlow<SmovBookVideoState> get() = _state.asStateFlow()

    //这个地方返回当前播放器所有数据
    fun <T> currentState(filter: (SmovBookVideoState) -> T): SmovBookVideoState {
        return SmovBookVideoState(
            state = state,
            screen = screen,
            widthRatio = widthRatio,
            heightRatio = heightRatio,
            mediaInterfaceClass = mediaInterfaceClass,
            positionInList = positionInList,
            videoRotation = videoRotation,
            seekToManulPosition = seekToManulPosition,
            seekToInAdvance = seekToInAdvance,
            preloading = preloading,
        )
    }

    override fun setUp(jzDataSource: JZDataSource, screen: Int) {
        super.setUp(jzDataSource, screen)
        titleTextView.visibility = INVISIBLE
    }

    override fun gotoFullscreen() {
        super.gotoFullscreen()
        titleTextView.visibility = VISIBLE
        changeFullscreen()
    }

    override fun gotoNormalScreen() {
        super.gotoNormalScreen()
        titleTextView.visibility = INVISIBLE
        changeFullscreen()
    }

}