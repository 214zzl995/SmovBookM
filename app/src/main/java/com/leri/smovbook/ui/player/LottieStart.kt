package com.leri.smovbook.ui.player

import android.animation.ValueAnimator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.leri.smovbook.R

@Composable
fun ComposeStart(
    state: Int,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.play_pause))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = -1F
    )

    Box(
        modifier = Modifier
            .size(55.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        LottieAnimation(composition = composition, progress = { progress })
    }

}