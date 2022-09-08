package com.leri.smovbook.ui.splash

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.leri.smovbook.R
import kotlinx.coroutines.delay


@Composable
fun SplashScreenLottie(navigateToHome: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.logo))

        val logoAnimationState = animateLottieCompositionAsState(composition = composition)

        LottieAnimation(
            composition = composition,
            progress = { logoAnimationState.progress },
            modifier = Modifier.fillMaxSize()
        )
        if (logoAnimationState.isAtEnd && logoAnimationState.isPlaying) {
            navigateToHome()
        }
    }
}

/**
 * 详细查看 https://dev.to/tgloureiro/the-definitive-guide-for-splash-screens-in-android-58e1
 */
@Composable
fun SplashScreen(navigateToHome: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f),
        contentAlignment = Alignment.Center
    ) {
        LaunchedEffect(key1 = true) {
            startAnimation = true
            delay(2000)
            navigateToHome()
        }
        val size = min(maxHeight, maxWidth) / 3

        val alphaAnim by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = tween(
                durationMillis = 500
            )
        )

        val offsetAnim by animateIntOffsetAsState(
            targetValue = if (startAnimation) IntOffset.Zero
                    else IntOffset(0,
                with(LocalDensity.current) {
                    (maxHeight - size).roundToPx()
                }),
            animationSpec = spring(0.4f, 880f),
        )

        Box(
            modifier = Modifier
                .size(size)
                .alpha(alpha = alphaAnim)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_smov_ico),
                contentDescription = "",
                modifier = Modifier.shadow(8.dp, RoundedCornerShape(6.dp))
            )
        }


    }
}

@Composable
fun SpringPlayground() {
    val dampingRatio = remember {
        mutableStateOf(Spring.DampingRatioNoBouncy)
    }
    val stiffness = remember {
        mutableStateOf(Spring.StiffnessMedium)
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        BoxWithConstraints(
            Modifier
                .fillMaxWidth()
                .weight(1f, true)
                .zIndex(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            val shape = RoundedCornerShape(6.dp)
            val size = min(maxHeight, maxWidth) / 3
            var onTop by remember {
                mutableStateOf(true)
            }
            val offset =
                animateIntOffsetAsState(
                    targetValue =
                    if (onTop) IntOffset.Zero
                    else
                        IntOffset(0,
                            with(LocalDensity.current) {
                                (maxHeight - size).roundToPx()
                            }),
                    spring(dampingRatio.value, stiffness.value)
                )
            Box(
                modifier = Modifier
                    .offset { offset.value }
                    .shadow(8.dp, shape)
                    .background(Color.Blue)
                    .size(size)
                    .clickable { onTop = onTop.not() }
            )

        }
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f, true)
                .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            ValueSlider(
                "Damping ratio",
                dampingRatio,
                Spring.DampingRatioHighBouncy..Spring.DampingRatioNoBouncy, Modifier
            )
            ValueSlider(
                "Stiffness",
                stiffness,
                Spring.StiffnessVeryLow..Spring.StiffnessMedium, Modifier
            )
        }
    }
}

@Composable
private fun ValueSlider(
    title: String,
    value: MutableState<Float>,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,

    ) {
    Column(modifier) {
        Text(title + ": ${value.value}", style = MaterialTheme.typography.bodySmall)
        Slider(value = value.value, onValueChange = { value.value = it }, valueRange = valueRange)
    }
}

@Composable
@Preview
fun SplashScreenPreview() {
    SplashScreen {}
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
fun SplashScreenDarkPreview() {
    SplashScreen {}
}

@Preview
@Composable
fun SpringPlaygroundPreview() {
    SpringPlayground()
}

