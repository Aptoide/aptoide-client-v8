package com.aptoide.android.aptoidegames.play_and_earn.presentation.components.animations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aptoide.android.aptoidegames.R

@Composable
fun PaEAnimatedGift(
  modifier: Modifier = Modifier
) {
  val composition by rememberLottieComposition(
    LottieCompositionSpec.RawRes(R.raw.gift)
  )

  val progress by animateLottieCompositionAsState(
    composition = composition,
    iterations = LottieConstants.IterateForever
  )

  Box(
    modifier = Modifier.wrapContentSize(),
    contentAlignment = Alignment.Center
  ) {
    LottieAnimation(
      modifier = Modifier
        .wrapContentSize(unbounded = true)
        .then(modifier),
      composition = composition,
      progress = { progress },
      contentScale = ContentScale.Crop,
    )
  }
}

@Preview
@Composable
private fun PaEAnimatedGiftPreview() {
  PaEAnimatedGift()
}
