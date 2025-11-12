package com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cm.aptoide.pt.appcoins.domain.WalletInfo
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.wallet.gamification.domain.GamificationStats
import cm.aptoide.pt.wallet.gamification.domain.Levels
import cm.aptoide.pt.wallet.gamification.domain.getCurrentLevelProgressRatio
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.drawables.backgrounds.getLevelUpBackground
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getLeaderboardIcon
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
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaECard
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaESectionHeader
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.animations.CurrentLevelAnimation
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.playAndEarnRewardsRoute
import com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange.exchangeUnitsRoute
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import java.math.BigDecimal
import java.math.RoundingMode

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
  val (levelUpState, reload) = rememberLevelUpState()
  val paeAnalytics = rememberPaEAnalytics()
  val lifecycleOwner = LocalLifecycleOwner.current

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        if (levelUpState != LevelUpUiState.Loading) {
          reload()
        }
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  when (levelUpState) {
    is LevelUpUiState.Idle -> LevelUpScreenContent(
      walletInfo = levelUpState.walletInfo,
      gamificationStats = levelUpState.gamificationStats,
      levels = levelUpState.levels,
      onBackClick = navigateBack,
      onPlayAndEarnCardClick = { navigate(playAndEarnRewardsRoute) },
      onExchangeClick = {
        paeAnalytics.sendPaEExchangeNowClick()
        navigate(exchangeUnitsRoute)
      }
    )

    LevelUpUiState.Loading -> {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        IndeterminateCircularLoading(color = Palette.Primary)
      }
    }

    LevelUpUiState.Error,
    LevelUpUiState.NoConnection -> GenericErrorView(onRetryClick = reload)
  }
}

@Composable
private fun LevelUpScreenContent(
  walletInfo: WalletInfo,
  gamificationStats: GamificationStats,
  levels: Levels,
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
      gamificationStats = gamificationStats,
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
        balance = walletInfo.walletBalance.amount,
        currency = walletInfo.walletBalance.currency,
      )
      UnitsCard(
        modifier = Modifier.weight(1f),
        availableUnits = walletInfo.unitsBalance
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    UnitsExchangeCard(
      availableUnits = walletInfo.unitsBalance,
      onExchangeClick = onExchangeClick
    )

    Spacer(modifier = Modifier.height(16.dp))

    UserLevelProgressSection(gamificationStats = gamificationStats, levels = levels)

    Spacer(modifier = Modifier.height(16.dp))

    PaECard(onClick = onPlayAndEarnCardClick)
  }
}

@Composable
private fun LevelUpHeaderSection(
  onBackClick: () -> Unit,
  gamificationStats: GamificationStats,
) {
  Box(
    modifier = Modifier.fillMaxWidth(),
    contentAlignment = Alignment.Center
  ) {
    Image(
      modifier = Modifier.fillMaxWidth(),
      imageVector = getLevelUpBackground(level = gamificationStats.level + 1),
      contentDescription = null,
      contentScale = ContentScale.FillWidth
    )
    CurrentLevelAnimation(level = gamificationStats.level + 1)

    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 28.dp)
    ) {
      CurrentLevelText(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        currentAmount = gamificationStats.totalSpend,
        nextLevelAmount = gamificationStats.nextLevelAmount
      )
      CurrentLevelBar(
        gamificationStats = gamificationStats,
        modifier = Modifier.align(Alignment.CenterHorizontally)
      )
    }
    Box(
      modifier = Modifier.align(Alignment.TopCenter)
    ) {
      AppGamesTopBar(
        navigateBack = onBackClick,
        title = stringResource(R.string.play_and_earn_level_up_title),
        iconColor = Palette.White
      )
    }
  }
}

@Composable
private fun CurrentLevelText(
  modifier: Modifier = Modifier,
  currentAmount: BigDecimal,
  nextLevelAmount: BigDecimal?
) {
  val annotatedString = buildAnnotatedString {
    withStyle(style = AGTypography.InputsM.toSpanStyle().copy(color = Palette.Yellow100)) {
      append(currentAmount.toRoundedString())
    }
    nextLevelAmount?.toRoundedString()?.let {
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
  gamificationStats: GamificationStats,
  modifier: Modifier = Modifier
) {
  var targetProgress by remember { mutableFloatStateOf(0f) }

  LaunchedEffect(Unit) {
    targetProgress = gamificationStats.getCurrentLevelProgressRatio()
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
        imageVector = getCurrentCoinIcon(gamificationStats.level + 1),
        contentDescription = null,
        modifier = Modifier
          .size(16.dp)
          .offset(x = (-8).dp)
      )
    }

    Image(
      imageVector = getCurrentCoinIcon((gamificationStats.level + 2).coerceIn(0, 9)),
      contentDescription = null,
      modifier = Modifier
        .size(32.dp)
        .offset(x = 16.dp)
        .align(Alignment.CenterEnd)
    )
  }
}

@Composable
private fun UserLevelProgressSection(
  gamificationStats: GamificationStats,
  levels: Levels
) {
  Column(
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .padding(top = 32.dp)
      .padding(bottom = 25.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    PaESectionHeader(
      icon = getLeaderboardIcon(),
      text = stringResource(R.string.play_and_earn_your_level_title)
    )
    UserLevelStateBar(levels = levels, gamificationStats = gamificationStats)
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

@Preview
@Composable
fun LevelUpScreenPreview() {
  LevelUpScreen(
    navigate = {},
    navigateBack = {}
  )
}

fun BigDecimal.toRoundedString() = this.setScale(0, RoundingMode.DOWN).toString()
