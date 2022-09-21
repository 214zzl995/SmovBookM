package com.leri.smovbook.ui.player

import android.content.Context
import android.util.AttributeSet
import cn.jzvd.JZDataSource
import cn.jzvd.JZDataSource.URL_KEY_DEFAULT
import cn.jzvd.JzvdStd

/**
 * Created by Leri
 * On 2022/09/19 22:24
 * 已经解决了全屏的问题 需要解决 全屏伸缩的问题 考虑重新创建一个视频的activity 还有返回后没有销毁的问题 需要重新做ui
 */
class SmovBookVideoActive : JzvdStd {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun setUp(jzDataSource: JZDataSource, screen: Int) {
        super.setUp(jzDataSource, screen)
        titleTextView.visibility = INVISIBLE
    }

    override fun gotoFullscreen() {
        super.gotoFullscreen()
        titleTextView.visibility = VISIBLE
    }

    override fun gotoNormalScreen() {
        super.gotoNormalScreen()
        titleTextView.visibility = INVISIBLE
    }

}