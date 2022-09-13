package com.leri.smovbook.ui.videoplayer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ui.StyledPlayerView

@Composable
fun PlayerSurface(
    modifier: Modifier = Modifier,
    onPlayerViewAvailable: (StyledPlayerView) -> Unit = {}
) {
    AndroidView(
        factory = { context ->
            StyledPlayerView(context).apply {
                useController = false
                onPlayerViewAvailable(this)
            }
        },
        modifier = modifier
    )
}


