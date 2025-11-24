package com.aptoide.android.aptoidegames.play_and_earn.presentation.components.animations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun RewardsStarsAnimation(
  modifier: Modifier = Modifier,
  contentScale: ContentScale = ContentScale.Crop
) {
  val composition by rememberLottieComposition(
    LottieCompositionSpec.RawRes(R.raw.rewards_stars)
  )

  val progress by animateLottieCompositionAsState(
    composition = composition,
    iterations = LottieConstants.IterateForever,
    speed = 0.7f
  )

  LottieAnimation(
    modifier = modifier,
    composition = composition,
    progress = { progress },
    contentScale = contentScale,
  )
}

@Preview
@Composable
private fun RewardsStarsAnimationPreview() {
  RewardsStarsAnimation()
}
