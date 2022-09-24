package com.leri.smovbook.ui.player

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SmovVideoState(
    val isIfCurrentIsFullscreen: Boolean = true,
    val title: String = "",
    val url: String = ""
) : Parcelable