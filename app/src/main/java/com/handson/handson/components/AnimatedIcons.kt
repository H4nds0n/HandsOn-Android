package com.handson.handson.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.handson.handson.R


@Composable
fun SuccessAnimation(modifier: Modifier = Modifier, onComplete: () -> Unit = {}) {
    val successLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.success_animation
        )
    )
    var isPlaying by remember {
        mutableStateOf(true)
    }

    val clipSpec = LottieClipSpec.Progress(0.0f, 0.8f)

    val animationProgress by animateLottieCompositionAsState(
        successLottieComposition,
        isPlaying = isPlaying,
        restartOnPlay = false,
        clipSpec = clipSpec
    )
    Log.d("animationP", animationProgress.toString())

    if (animationProgress == 0.8f) {
        Log.d("animationP", animationProgress.toString())
        onComplete()
    }

    LottieAnimation(
        composition = successLottieComposition,
        progress = { animationProgress },
        modifier = modifier,
    )
}


@Composable
fun DeleteAnimation(modifier: Modifier = Modifier, onComplete: () -> Unit = {}) {
    val deleteLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.delete_animation
        )
    )


    val animationProgress by animateLottieCompositionAsState(
        deleteLottieComposition,
        isPlaying = true,
        restartOnPlay = true,
        speed = 1.5f
    )
    // LaunchedEffect(key1 = animationProgress) {
    Log.d("animationP", animationProgress.toString())
    //}

    if (animationProgress == 1.0f) {
        Log.d("animationP", animationProgress.toString())
        onComplete()
    }

    LottieAnimation(
        composition = deleteLottieComposition,
        progress = { animationProgress },
        modifier = modifier,

    )
}