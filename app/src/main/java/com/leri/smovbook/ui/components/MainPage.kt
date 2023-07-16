package com.leri.smovbook.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.*
import com.leri.smovbook.R
import com.leri.smovbook.models.network.NetworkState


@Composable
fun MainPage(
    pageState: NetworkState,
    content: @Composable () -> Unit,
) {
    Crossfade(targetState = pageState, label = "") { screen ->
        when (screen) {
            NetworkState.LOADING -> {
                FullScreenLoading()
            }
            NetworkState.IDLE -> {}
            NetworkState.SUCCESS -> {
                content()
            }
            NetworkState.ERROR -> {
                WrongRequest()
            }
        }
    }
}

@Composable
fun WrongRequest() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error_roadblock))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        LottieAnimation(composition = composition)
    }

}

@Composable
fun FullScreenLoading() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_40_paperplane))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        LottieAnimation(composition = composition, progress = { progress })
    }

}