package com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.play_and_earn.exchange.domain.UNITS_EXCHANGE_THRESHOLD
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.AccentButton
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.drawables.backgrounds.getExchangeSuccessBackground
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.home.rememberRewardsDestination
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.iconRes
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.rememberPreferredPaEReward
import com.aptoide.android.aptoidegames.play_and_earn.presentation.units.UnitsBalanceUiState
import com.aptoide.android.aptoidegames.play_and_earn.presentation.units.UnitsBalanceUiStateProvider
import com.aptoide.android.aptoidegames.play_and_earn.presentation.units.rememberUnitsBalanceUiState
import com.aptoide.android.aptoidegames.play_and_earn.presentation.units.rememberUnitsExchangeThreshold
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.FixedColors
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import java.math.BigDecimal
import java.math.RoundingMode

const val redeemRewardRoute = "redeemReward"

private val REDEEM_AMOUNT: BigDecimal = BigDecimal("5.00")

fun redeemRewardScreen(
  navigateToPickReward: (String) -> Unit,
) = ScreenData.withAnalytics(
  route = redeemRewardRoute,
  screenAnalyticsName = "RedeemReward",
) { _, navigate, navigateBack ->
  val rewardType = rememberPreferredPaEReward()
  val uiState = rememberUnitsBalanceUiState()
  val rewardsDestination = rememberRewardsDestination()
  val paeAnalytics = rememberPaEAnalytics()

  LaunchedEffect(Unit) {
    paeAnalytics.sendPaEBalanceScreenShown()
  }

  RedeemRewardScreen(
    rewardType = rewardType,
    uiState = uiState,
    targetUnits = rememberUnitsExchangeThreshold(),
    redeemAmount = REDEEM_AMOUNT,
    navigateBack = navigateBack,
    onRedeem = {
      paeAnalytics.sendPaERedeemClick()
      navigateToPickReward(it)
    },
    onEarnMore = {
      paeAnalytics.sendPaERedeemEarnMoreClick()
      navigate(rewardsDestination)
    },
  )
}

@Composable
fun RedeemRewardScreen(
  rewardType: PaERewardType,
  uiState: UnitsBalanceUiState,
  targetUnits: Long,
  redeemAmount: BigDecimal,
  navigateBack: () -> Unit,
  onRedeem: (String) -> Unit,
  onEarnMore: () -> Unit,
) {
  ExchangeFlowBackground {
    Column(modifier = Modifier.fillMaxSize()) {
      AppGamesTopBar(
        navigateBack = navigateBack,
        title = stringResource(R.string.play_and_earn_title),
        iconColor = Palette.White,
      )

      when (uiState) {
        UnitsBalanceUiState.Loading -> LoadingContent()

        is UnitsBalanceUiState.Error -> GenericErrorView(onRetryClick = uiState.retry)

        is UnitsBalanceUiState.Idle -> RedeemRewardContent(
          rewardType = rewardType,
          availableUnits = uiState.availableUnits,
          targetUnits = targetUnits,
          redeemAmount = redeemAmount,
          onRedeem = onRedeem,
          onEarnMore = onEarnMore,
        )
      }
    }
  }
}

@Composable
private fun LoadingContent() {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    IndeterminateCircularLoading(color = Palette.Primary)
  }
}

@Composable
private fun RedeemRewardContent(
  rewardType: PaERewardType,
  availableUnits: Long,
  targetUnits: Long,
  redeemAmount: BigDecimal,
  onRedeem: (String) -> Unit,
  onEarnMore: () -> Unit,
) {
  val hasEnough = availableUnits >= targetUnits
  val unitsToGo = (targetUnits - availableUnits).coerceAtLeast(0L)
  val formattedRedeem = "$${redeemAmount.setScale(2, RoundingMode.HALF_UP).toPlainString()}"

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    Spacer(modifier = Modifier.height(24.dp))

    Text(
      modifier = Modifier.align(Alignment.CenterHorizontally),
      text = stringResource(R.string.play_and_earn_exchange_your_balance_label),
      style = AGTypography.InputsL,
      color = Palette.White,
    )

    Spacer(modifier = Modifier.height(8.dp))

    UnitsHexagonHero(units = availableUnits, isComplete = hasEnough)

    Spacer(modifier = Modifier.height(24.dp))

    RedeemOfferCard(
      rewardType = rewardType,
      formattedRedeem = formattedRedeem,
      costUnits = targetUnits,
      balanceAfter = (availableUnits - targetUnits).coerceAtLeast(0L),
      unitsToGo = unitsToGo,
      hasEnough = hasEnough,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
    )

    Spacer(modifier = Modifier.weight(1f))

    AccentButton(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 24.dp),
      title = if (hasEnough) {
        stringResource(R.string.play_and_earn_exchange_redeem_button, formattedRedeem)
      } else {
        stringResource(R.string.play_and_earn_earn_more_button)
      },
      enabled = true,
      onClick = { if (hasEnough) onRedeem(formattedRedeem) else onEarnMore() },
    )
  }
}

@Composable
private fun UnitsHexagonHero(units: Long, isComplete: Boolean = false) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 64.dp),
    contentAlignment = Alignment.Center,
  ) {
    if (isComplete) {
      Image(
        imageVector = getExchangeSuccessBackground(Palette.Yellow50),
        contentDescription = null,
        modifier = Modifier
          .size(0.dp)
          .wrapContentSize(unbounded = true),
        contentScale = ContentScale.Fit,
      )
    }
    Image(
      painter = painterResource(R.drawable.exchange_screen_icon),
      contentDescription = null,
      contentScale = ContentScale.Fit,
    )
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = units.toString(),
        style = AGTypography.Title.copy(fontSize = 35.sp),
        color = Palette.Black,
        textAlign = TextAlign.Center,
      )
      Text(
        text = stringResource(R.string.play_and_earn_exchange_units_label),
        style = AGTypography.Body.copy(fontSize = 14.sp),
        color = Palette.Black,
        textAlign = TextAlign.Center,
      )
    }
  }
}

@Composable
private fun RedeemOfferCard(
  rewardType: PaERewardType,
  formattedRedeem: String,
  costUnits: Long,
  balanceAfter: Long,
  unitsToGo: Long,
  hasEnough: Boolean,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .background(FixedColors.PaeExchangePurple)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Image(
        painter = painterResource(rewardType.iconRes),
        contentDescription = null,
        modifier = Modifier.size(40.dp),
      )
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = "$formattedRedeem in ${rewardType.exchangeDisplayName}",
          style = AGTypography.SubHeadingM,
          color = Palette.White,
        )
        Text(
          text = stringResource(R.string.play_and_earn_exchange_redeem_cost, costUnits),
          style = AGTypography.InputsM,
          color = Palette.White,
        )
      }
    }

    Divider(color = Palette.White.copy(alpha = 0.1f), thickness = 1.dp)

    if (hasEnough) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = stringResource(R.string.play_and_earn_exchange_balance_after),
          style = AGTypography.InputsL,
          color = Palette.White,
        )
        Text(
          text = stringResource(R.string.play_and_earn_exchange_units_value, balanceAfter),
          style = AGTypography.InputsL,
          color = Palette.Primary,
        )
      }
    } else {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = stringResource(R.string.play_and_earn_exchange_not_enough_balance),
          style = AGTypography.InputsL,
          color = Palette.White,
        )
        Text(
          text = stringResource(R.string.play_and_earn_exchange_units_to_go, unitsToGo),
          style = AGTypography.InputsL,
          color = Palette.Yellow100,
        )
      }
    }
  }
}

@PreviewDark
@Composable
private fun RedeemRewardScreenPreview(
  @PreviewParameter(UnitsBalanceUiStateProvider::class) uiState: UnitsBalanceUiState.Idle,
) {
  RedeemRewardScreen(
    rewardType = PaERewardType.ROBUX,
    uiState = uiState,
    targetUnits = UNITS_EXCHANGE_THRESHOLD,
    redeemAmount = REDEEM_AMOUNT,
    navigateBack = {},
    onRedeem = {},
    onEarnMore = {},
  )
}
