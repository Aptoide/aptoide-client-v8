package com.aptoide.android.aptoidegames.play_and_earn

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.backgrounds.getLevelUpBackground
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getPlayAndEarnSmallLogo
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.getLevelEightCoinIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.getLevelFiveCoinIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.getLevelFourCoinIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.getLevelNiceCoinIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.getLevelOneCoinIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.getLevelSevenCoinIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.getLevelSixCoinIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.getLevelTenCoinIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.getLevelThreeCoinIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.getLevelTwoCoinIcon
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun LevelUpScreen() {
  val currentLevel = Random.nextInt(1..10)

  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    Box(
      modifier = Modifier.fillMaxWidth(),
      contentAlignment = Alignment.Center
    ) {
      Image(
        modifier = Modifier.fillMaxWidth(),
        imageVector = getLevelUpBackground(level = currentLevel),
        contentDescription = null,
        contentScale = ContentScale.FillWidth
      )
      CurrentLevelAnimation(level = currentLevel)

      Column(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 28.dp)
      ) {
        CurrentLevelText(
          modifier = Modifier.align(Alignment.CenterHorizontally),
          currentPoints = 5600, checkpointPoints = 6750
        )
        CurrentLevelBar(
          currentLevel = currentLevel,
          progress = 0.6f,
          modifier = Modifier.align(Alignment.CenterHorizontally)
        )
      }
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      BalanceCard(
        modifier = Modifier.weight(1f),
        balance = 2f
      )
      UnitsCard(
        modifier = Modifier.weight(1f),
        availableUnits = 75
      )
    }
  }
}

@Composable
private fun CurrentLevelText(
  modifier: Modifier = Modifier,
  currentPoints: Int,
  checkpointPoints: Int
) {
  val annotatedString = buildAnnotatedString {
    withStyle(style = AGTypography.InputsM.toSpanStyle().copy(color = Palette.Yellow)) {
      append(currentPoints.toString())
    }
    withStyle(style = AGTypography.InputsXSRegular.toSpanStyle().copy(color = Palette.White)) {
      append("/$checkpointPoints")
    }
  }

  Text(
    modifier = modifier,
    text = annotatedString
  )
}

@Composable
private fun CurrentLevelBar(
  currentLevel: Int,
  progress: Float,
  modifier: Modifier = Modifier
) {
  var targetProgress by remember { mutableFloatStateOf(0f) }

  LaunchedEffect(Unit) {
    targetProgress = progress
  }

  //LinearOutSlowInEasing

  val animatedProgress by animateFloatAsState(
    targetValue = targetProgress.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 2000, easing = EaseOut),
    label = "progress"
  )

  Box(
    modifier = modifier.width(214.dp),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .height(8.dp)
        .fillMaxWidth()
        .background(Palette.Grey)
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .height(8.dp)
          .fillMaxWidth(animatedProgress)
          .background(color = Color.White)
          .background(
            brush = Brush.horizontalGradient(
              *arrayOf(
                0.8f to Color(0xFFFFC93E),
                1f to Color(0xFFFFC93E).copy(alpha = 0.2f)
              )
            )
          )
      )

      Image(
        imageVector = getCurrentCoinIcon(currentLevel),
        contentDescription = null,
        modifier = Modifier
          .size(16.dp)
          .offset(x = (-8).dp)
      )
    }

    Image(
      imageVector = getCurrentCoinIcon(currentLevel + 1),
      contentDescription = null,
      modifier = Modifier
        .size(32.dp)
        .offset(x = 16.dp)
        .align(Alignment.CenterEnd)
    )
  }
}

@Composable
private fun CurrentLevelAnimation(
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

@Composable
private fun BalanceCard(
  balance: Float,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .requiredHeight(158.dp)
      .background(Palette.GreyDark),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = "Aptoide Balance", //TODO: hardcoded string
        style = AGTypography.InputsM,
        color = Palette.White,
      )
      Image(
        imageVector = getPlayAndEarnSmallLogo(color1 = Palette.White, color2 = Palette.GreyDark),
        contentDescription = null,
        modifier = Modifier.size(58.dp)
      )
      Text(
        text = "$${balance}", //TODO: hardcoded string
        style = AGTypography.InputsL,
        color = Palette.White,
      )
    }
  }
}

private fun getCurrentCoinIcon(level: Int): ImageVector = when (level) {
  1 -> getLevelOneCoinIcon()
  2 -> getLevelTwoCoinIcon()
  3 -> getLevelThreeCoinIcon()
  4 -> getLevelFourCoinIcon()
  5 -> getLevelFiveCoinIcon()
  6 -> getLevelSixCoinIcon()
  7 -> getLevelSevenCoinIcon()
  8 -> getLevelEightCoinIcon()
  9 -> getLevelNiceCoinIcon()
  10 -> getLevelTenCoinIcon()
  else -> getLevelOneCoinIcon()
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
fun LevelUpScreenPreview() {
  LevelUpScreen()
}
