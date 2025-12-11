package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCancellationBehavior
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aptoide.android.aptoidegames.R

@Composable
fun AnimationComposable(
  resId: Int,
  modifier: Modifier = Modifier,
  isSelected: Boolean = false,
  selectedResId: Int = R.raw.game_genie_bottom_bar_icon,
) {
  val composition by rememberLottieComposition(
    LottieCompositionSpec.RawRes(resId)
  )

  val selectedComposition by rememberLottieComposition(
    LottieCompositionSpec.RawRes(selectedResId)
  )

  val progress by animateLottieCompositionAsState(
    composition = composition,
    iterations = LottieConstants.IterateForever,
    cancellationBehavior = LottieCancellationBehavior.OnIterationFinish,
    isPlaying = true
  )

  val selectedProgress by animateLottieCompositionAsState(
    composition = selectedComposition,
    iterations = LottieConstants.IterateForever,
    cancellationBehavior = LottieCancellationBehavior.OnIterationFinish,
    isPlaying = true
  )

  Box(
    modifier = Modifier.wrapContentSize(),
    contentAlignment = Alignment.Center
  ) {
    if (isSelected) {
      LottieAnimation(
        modifier = Modifier
          .wrapContentSize(unbounded = true)
          .then(modifier),
        composition = selectedComposition,
        progress = { selectedProgress },
        contentScale = ContentScale.Crop,
      )
    } else {
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
}
