package com.aptoide.android.aptoidegames.play_and_earn.animations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aptoide.android.aptoidegames.R

@Composable
fun SingleUnitExchangeAnimation(modifier: Modifier = Modifier) {
  val composition by rememberLottieComposition(
    LottieCompositionSpec.RawRes(R.raw.unit_exchange)
  )

  val progress by animateLottieCompositionAsState(
    composition = composition,
    iterations = LottieConstants.IterateForever,
    clipSpec = LottieClipSpec.Progress(0f, 0.38f),
    reverseOnRepeat = true
  )

  LottieAnimation(
    modifier = modifier,
    composition = composition,
    progress = { progress },
    contentScale = ContentScale.Fit,
  )
}

@Composable
fun MultipleUnitExchangeAnimation(modifier: Modifier = Modifier) {
  val composition by rememberLottieComposition(
    LottieCompositionSpec.RawRes(R.raw.multiple_unit_exchange)
  )

  val progress by animateLottieCompositionAsState(
    composition = composition,
    iterations = LottieConstants.IterateForever,
    clipSpec = LottieClipSpec.Progress(0f, 0.38f),
    reverseOnRepeat = true
  )

  LottieAnimation(
    modifier = modifier,
    composition = composition,
    progress = { progress },
    contentScale = ContentScale.Fit,
  )
}

@Preview
@Composable
private fun SingleUnitAnimationPreview() {
  SingleUnitExchangeAnimation()
}

@Preview
@Composable
private fun MultipleUnitAnimationPreview() {
  MultipleUnitExchangeAnimation()
}
