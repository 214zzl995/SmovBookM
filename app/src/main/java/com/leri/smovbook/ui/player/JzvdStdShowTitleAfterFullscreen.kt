package com.leri.smovbook.ui.player

import android.content.Context
import android.util.AttributeSet
import cn.jzvd.JZDataSource
import cn.jzvd.JzvdStd

/**
 * Created by Leri
 * On 2022/09/19 22:24
 */
class JzvdStdShowTitleAfterFullscreen : JzvdStd {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    lateinit var changeFullscreen: () -> Unit

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