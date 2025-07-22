package com.aptoide.android.aptoidegames.play_and_earn.presentation.components.animations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aptoide.android.aptoidegames.R
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun CurrentLevelAnimation(
  modifier: Modifier = Modifier,
  level: Int
) {
  getCurrentLevelAnimationId(level)?.let {
    val composition by rememberLottieComposition(
      LottieCompositionSpec.RawRes(it)
    )

    val progress by animateLottieCompositionAsState(
      composition = composition,
      iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
      composition = composition,
      progress = { progress },
      modifier = modifier,
      contentScale = ContentScale.Crop,
    )
  } ?: Box(modifier = modifier.size(167.dp, 118.dp))
}

private fun getCurrentLevelAnimationId(level: Int): Int? = when (level) {
  1 -> R.raw.level_one
  2 -> R.raw.level_two
  3 -> R.raw.level_three
  4 -> R.raw.level_four
  5 -> R.raw.level_five
  6 -> R.raw.level_six
  7 -> R.raw.level_seven
  8 -> R.raw.level_eight
  9 -> R.raw.level_nine
  10 -> R.raw.level_ten
  else -> null
}

@Preview
@Composable
private fun CurrentLevelAnimationPreview() {
  CurrentLevelAnimation(
    level = Random.nextInt(1..10),
  )
}
