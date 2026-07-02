package com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.extensions.toAnnotatedString
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.AccentButton
import com.aptoide.android.aptoidegames.drawables.backgrounds.getExchangeSuccessBackground
import com.aptoide.android.aptoidegames.home.rememberRewardsDestination
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardSuccessArt
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

private const val exchangeSuccessBase = "exchangeSuccess"
private const val rewardTypeArg = "rewardType"
private const val amountArg = "amount"
private const val emailArg = "email"
const val exchangeSuccessRoute =
  "$exchangeSuccessBase/{$rewardTypeArg}/{$amountArg}/{$emailArg}"

fun buildExchangeSuccessRoute(
  rewardType: PaERewardType,
  formattedAmount: String,
  email: String,
) = "$exchangeSuccessBase/${rewardType.name}/${Uri.encode(formattedAmount)}/${Uri.encode(email)}"

fun exchangeSuccessScreen() = ScreenData.withAnalytics(
  route = exchangeSuccessRoute,
  screenAnalyticsName = "ExchangeSuccess",
  arguments = listOf(
    navArgument(rewardTypeArg) { type = NavType.StringType },
    navArgument(amountArg) { type = NavType.StringType },
    navArgument(emailArg) { type = NavType.StringType },
  ),
) { args, navigate, navigateBack ->
  val rewardType = args?.getString(rewardTypeArg)
    ?.let { runCatching { PaERewardType.valueOf(it) }.getOrNull() }
    ?: PaERewardType.ROBUX
  val formattedAmount = args?.getString(amountArg)?.let { Uri.decode(it) }.orEmpty()
  val email = args?.getString(emailArg)?.let { Uri.decode(it) }.orEmpty()
  val rewardsDestination = rememberRewardsDestination()
  val paeAnalytics = rememberPaEAnalytics()

  ExchangeSuccessScreen(
    rewardType = rewardType,
    formattedAmount = formattedAmount,
    email = email,
    navigateBack = navigateBack,
    onEarnMore = {
      paeAnalytics.sendPaEExchangeSuccessEarnMoreClick()
      navigate(rewardsDestination)
    },
  )
}

@Composable
fun ExchangeSuccessScreen(
  rewardType: PaERewardType,
  formattedAmount: String,
  email: String,
  navigateBack: () -> Unit,
  onEarnMore: () -> Unit,
) {
  ExchangeFlowBackground {
    Column(modifier = Modifier.fillMaxSize()) {
      AppGamesTopBar(
        navigateBack = navigateBack,
        title = "",
        iconColor = Palette.White,
      )

      Spacer(modifier = Modifier.weight(1f))

      Box(contentAlignment = Alignment.Center) {
        Image(
          imageVector = getExchangeSuccessBackground(
            when (rewardType) {
              PaERewardType.ROBUX -> Palette.Yellow50
              PaERewardType.DIAMONDS -> Palette.Official
            }
          ),
          contentDescription = null,
          modifier = Modifier
            .size(0.dp)
            .wrapContentSize(unbounded = true),
        )
        PaERewardSuccessArt(
          paERewardType = rewardType,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Center,
          text = stringResource(
            R.string.play_and_earn_reward_claim_message,
            formattedAmount,
            rewardType.exchangeDisplayName,
          ).toAnnotatedString(
            AGTypography.Title.toSpanStyle().copy(color = Palette.Yellow100)
          ),
          style = AGTypography.Title,
          color = Palette.White,
        )
        Text(
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Center,
          text = stringResource(R.string.play_and_earn_exchange_success_email_sent, email),
          style = AGTypography.SubHeadingS,
          color = Palette.White,
        )
      }

      Spacer(modifier = Modifier.weight(1f))

      AccentButton(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
          .padding(bottom = 24.dp),
        title = stringResource(R.string.play_and_earn_exchange_earn_more_button),
        onClick = onEarnMore,
      )
    }
  }
}

@PreviewDark
@Composable
private fun ExchangeSuccessRobuxPreview() {
  ExchangeSuccessScreen(
    rewardType = PaERewardType.ROBUX,
    formattedAmount = "$5.00",
    email = "user@example.com",
    navigateBack = {},
    onEarnMore = {},
  )
}

@PreviewDark
@Composable
private fun ExchangeSuccessDiamondsPreview() {
  ExchangeSuccessScreen(
    rewardType = PaERewardType.DIAMONDS,
    formattedAmount = "$5.00",
    email = "user@example.com",
    navigateBack = {},
    onEarnMore = {},
  )
}
