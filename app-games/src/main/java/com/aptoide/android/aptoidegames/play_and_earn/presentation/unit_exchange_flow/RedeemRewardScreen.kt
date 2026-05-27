package com.aptoide.android.aptoidegames.play_and_earn.presentation.units

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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import cm.aptoide.pt.extensions.toAnnotatedString
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.AccentButton
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.drawables.backgrounds.getExchangeSuccessBackground
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up.UnitsBar
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.iconRes
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.rememberPreferredPaEReward
import com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow.ExchangeFlowBackground
import com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow.exchangeDisplayName
import com.aptoide.android.aptoidegames.theme.AGTypography
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
) { _, _, navigateBack ->
  val rewardType = rememberPreferredPaEReward()
  val uiState = rememberUnitsBalanceUiState()

  RedeemRewardScreen(
    rewardType = rewardType,
    uiState = uiState,
    targetUnits = UNITS_EXCHANGE_THRESHOLD,
    redeemAmount = REDEEM_AMOUNT,
    navigateBack = navigateBack,
    onRedeem = navigateToPickReward,
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
) {
  val isComplete = availableUnits >= targetUnits
  val formattedRedeem = "$${redeemAmount.setScale(2, RoundingMode.HALF_UP).toPlainString()}"

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    Spacer(modifier = Modifier.height(24.dp))

    UnitsHexagonHero(units = availableUnits, isComplete = isComplete)

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 30.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Image(
        painter = painterResource(rewardType.iconRes),
        contentDescription = null,
        modifier = Modifier.size(24.dp),
      )
      Text(
        text = "$formattedRedeem in ${rewardType.exchangeDisplayName}",
        style = AGTypography.SubHeadingM,
        color = Palette.White,
        textAlign = TextAlign.Center,
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    UnitsBar(
      availableUnits = availableUnits,
      barHeight = 12.dp,
      targetUnits = targetUnits,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 30.dp),
    )

    Spacer(modifier = Modifier.height(24.dp))

    UnitsProgressLabel(
      currentUnits = availableUnits,
      targetUnits = targetUnits,
      modifier = Modifier.align(Alignment.CenterHorizontally),
    )

    Spacer(modifier = Modifier.weight(1f))

    if (!isComplete) {
      RewardInfoBanner(
        variant = rewardType,
        targetUnits = targetUnits,
        formattedTargetAmount = formattedRedeem,
      )
    }

    AccentButton(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 24.dp),
      title = if (isComplete) {
        stringResource(R.string.play_and_earn_exchange_redeem_button, formattedRedeem)
      } else {
        stringResource(
          R.string.play_and_earn_exchange_units_to_go,
          (targetUnits - availableUnits).coerceAtLeast(0L),
        )
      },
      enabled = isComplete,
      onClick = { onRedeem(formattedRedeem) },
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
private fun UnitsProgressLabel(
  currentUnits: Long,
  targetUnits: Long,
  modifier: Modifier = Modifier,
) {
  val originalString = stringResource(
    id = R.string.play_and_earn_exchange_units_progress,
    currentUnits,
    targetUnits,
  )
  val annotatedString = originalString.toAnnotatedString(
    AGTypography.Title.toSpanStyle().copy(color = Palette.White)
  )

  Text(
    modifier = modifier,
    text = annotatedString,
    style = AGTypography.InputsXSRegular,
    color = Palette.White,
  )
}

@Composable
private fun RewardInfoBanner(
  variant: PaERewardType,
  targetUnits: Long,
  formattedTargetAmount: String,
) {
  val text = when (variant) {
    PaERewardType.ROBUX -> stringResource(
      R.string.play_and_earn_exchange_reach_target_robux,
      targetUnits,
      formattedTargetAmount,
    )

    PaERewardType.DIAMONDS -> stringResource(
      R.string.play_and_earn_exchange_reach_target_diamonds,
      targetUnits,
      formattedTargetAmount,
    )
  }
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp)
      .background(Palette.Secondary.copy(alpha = 0.2f))
      .padding(16.dp),
  ) {
    Text(
      text = text,
      style = AGTypography.Body,
      color = Palette.White,
      textAlign = TextAlign.Center,
      modifier = Modifier.fillMaxWidth(),
    )
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
  )
}
