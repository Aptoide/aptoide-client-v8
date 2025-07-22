package com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.ScreenData
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.AccentSmallButton
import com.aptoide.android.aptoidegames.drawables.backgrounds.getLevelUpBackground
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getGiftIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getLeaderboardIcon
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
import com.aptoide.android.aptoidegames.play_and_earn.domain.Level
import com.aptoide.android.aptoidegames.play_and_earn.domain.UserStats
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PlayAndEarnCard
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PlayAndEarnSectionHeader
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.playAndEarnRewardsRoute
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import java.util.Locale

const val levelUpRoute = "levelUp"

fun levelUpScreen() = ScreenData.withAnalytics(
  route = levelUpRoute,
  screenAnalyticsName = "LevelUp",
) { _, navigate, navigateBack ->

  LevelUpScreen(navigate = navigate, navigateBack = navigateBack)
}

@Composable
fun LevelUpScreen(
  navigate: (String) -> Unit,
  navigateBack: () -> Unit
) {
  val levelUpState = rememberLevelUpState()

  when (levelUpState) {
    is LevelUpUiState.Idle -> LevelUpScreenContent(
      userStats = levelUpState.userStats,
      availableUnits = levelUpState.availableUnits,
      balance = levelUpState.balance,
      levels = levelUpState.levels,
      onBackClick = navigateBack,
      onPlayAndEarnCardClick = { navigate(playAndEarnRewardsRoute) },
      onExchangeClick = {}
    )

    else -> {}
  }
}

@Composable
private fun LevelUpScreenContent(
  userStats: UserStats,
  availableUnits: Int,
  balance: Float,
  levels: List<Level>,
  onBackClick: () -> Unit,
  onPlayAndEarnCardClick: () -> Unit,
  onExchangeClick: () -> Unit
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(bottom = 66.dp),
  ) {
    LevelUpHeaderSection(
      onBackClick = onBackClick,
      userStats = userStats,
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      BalanceCard(
        modifier = Modifier.weight(1f),
        balance = balance
      )
      UnitsCard(
        modifier = Modifier.weight(1f),
        availableUnits = availableUnits
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    UnitsExchangeCard(
      availableUnits = availableUnits,
      onExchangeClick = onExchangeClick
    )

    Spacer(modifier = Modifier.height(16.dp))

    UserLevelProgressSection(userStats = userStats, levels = levels)

    Spacer(modifier = Modifier.height(16.dp))

    PlayAndEarnCard(onClick = onPlayAndEarnCardClick)
  }
}

@Composable
private fun LevelUpHeaderSection(
  onBackClick: () -> Unit,
  userStats: UserStats,
) {
  Box(
    modifier = Modifier.fillMaxWidth(),
    contentAlignment = Alignment.Center
  ) {
    Image(
      modifier = Modifier.fillMaxWidth(),
      imageVector = getLevelUpBackground(level = userStats.level + 1),
      contentDescription = null,
      contentScale = ContentScale.FillWidth
    )
    CurrentLevelAnimation(level = userStats.level + 1)

    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 28.dp)
    ) {
      CurrentLevelText(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        currentAmount = userStats.currentAmount,
        nextLevelAmount = userStats.nextLevelAmount
      )
      CurrentLevelBar(
        userStats = userStats,
        modifier = Modifier.align(Alignment.CenterHorizontally)
      )
    }
    Box(
      modifier = Modifier.align(Alignment.TopCenter)
    ) {
      AppGamesTopBar(navigateBack = onBackClick, title = "Level Up", iconColor = Palette.White)
    }
  }
}

@Composable
private fun UserLevelProgressSection(
  userStats: UserStats,
  levels: List<Level>
) {
  Column(
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .padding(top = 32.dp)
      .padding(bottom = 25.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    PlayAndEarnSectionHeader(icon = getLeaderboardIcon(), text = "Your Level")
    UserLevelStateBar(levels = levels, userStats = userStats)
  }
}

@Composable
private fun CurrentLevelText(
  modifier: Modifier = Modifier,
  currentAmount: Float,
  nextLevelAmount: Long?
) {
  val annotatedString = buildAnnotatedString {
    withStyle(style = AGTypography.InputsM.toSpanStyle().copy(color = Palette.Yellow100)) {
      append(currentAmount.toLong().toString())
    }
    nextLevelAmount?.let {
      withStyle(style = AGTypography.InputsXSRegular.toSpanStyle().copy(color = Palette.White)) {
        append("/$it")
      }
    }
  }

  Text(
    modifier = modifier,
    text = annotatedString
  )
}

@Composable
private fun CurrentLevelBar(
  userStats: UserStats,
  modifier: Modifier = Modifier
) {
  var targetProgress by remember { mutableFloatStateOf(0f) }

  LaunchedEffect(Unit) {
    targetProgress = if (userStats.nextLevelAmount != null) {
      (userStats.currentAmount / userStats.nextLevelAmount)
    } else {
      1f
    }
  }

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
        imageVector = getCurrentCoinIcon(userStats.level + 1),
        contentDescription = null,
        modifier = Modifier
          .size(16.dp)
          .offset(x = (-8).dp)
      )
    }

    Image(
      imageVector = getCurrentCoinIcon((userStats.level + 2).coerceIn(0, 9)),
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
        text = "$${String.format(Locale.getDefault(), "%.2f", balance)}", //TODO: hardcoded string
        style = AGTypography.InputsL,
        color = Palette.White,
      )
    }
  }
}

@Composable
private fun UnitsExchangeCard(
  availableUnits: Int,
  onExchangeClick: () -> Unit
) {
  if (availableUnits < 100) {
    InsufficientUnitsCard(availableUnits)
  } else {
    ExchangeableUnitsCard(
      availableUnits = availableUnits,
      onExchangeClick = onExchangeClick
    )
  }
}

@Composable
private fun InsufficientUnitsCard(availableUnits: Int) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .background(Palette.Secondary.copy(alpha = 0.3f))
      .border(2.dp, Palette.Secondary)
  ) {
    Row(
      modifier = Modifier.padding(all = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Image(
        painter = painterResource(R.drawable.coins_stack),
        contentDescription = null
      )
      Text(
        text = "Collect ${100 - availableUnits} more units to earn \$2.00 in Aptoide Balance.",
        style = AGTypography.InputsS,
        color = Palette.White
      )
    }
  }
}

@Composable
private fun ExchangeableUnitsCard(
  availableUnits: Int,
  onExchangeClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .background(Palette.Secondary.copy(alpha = 0.3f))
      .border(2.dp, Palette.Secondary)
  ) {
    Column(
      modifier = Modifier.padding(all = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box {
          Image(
            painter = painterResource(R.drawable.coins_stack),
            contentDescription = null
          )
          Box(
            modifier = Modifier.padding(start = 38.dp),
            contentAlignment = Alignment.Center
          ) {
            Image(
              imageVector = unitsMultiplierBackground(),
              contentDescription = null
            )
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Image(
                imageVector = getGiftIcon(),
                contentDescription = null,
                modifier = Modifier.size(14.dp, 16.dp)
              )
              Text(
                text = "x${availableUnits / 100}",
                style = AGTypography.InputsS,
                color = Palette.White
              )
            }
          }
        }
        ((availableUnits / 100) * 100).let { units ->
          Text(
            text = "Exchange $units units for \$${
              (units * 2f / 100f).let {
                String.format(Locale.getDefault(), "%.2f", it)
              }
            } in your Aptoide Balance.",
            style = AGTypography.InputsS,
            color = Palette.White,
            textAlign = TextAlign.Center
          )
        }
      }
      AccentSmallButton(
        title = "Exchange Now", //TODO: hardcoded string
        onClick = onExchangeClick,
        modifier = Modifier.fillMaxWidth(),
      )
    }
  }
}

private fun unitsMultiplierBackground() = ImageVector.Builder(
  defaultWidth = 44.0.dp,
  defaultHeight = 24.0.dp,
  viewportWidth = 44.0f,
  viewportHeight = 24.0f
).apply {
  path(
    fill = SolidColor(Color(0xFF913DD8)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(0.0f, 16.0f)
    curveTo(0.0f, 7.163f, 7.163f, 0.0f, 16.0f, 0.0f)
    horizontalLineTo(32.0f)
    curveTo(38.627f, 0.0f, 44.0f, 5.373f, 44.0f, 12.0f)
    verticalLineTo(12.0f)
    curveTo(44.0f, 18.627f, 38.627f, 24.0f, 32.0f, 24.0f)
    horizontalLineTo(0.0f)
    verticalLineTo(16.0f)
    close()
  }
}.build()

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
  LevelUpScreen(
    navigate = {},
    navigateBack = {}
  )
}
