package com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.drawables.icons.getForward
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.iconRes
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

private const val pickRewardBase = "pickReward"
private const val amountArg = "amount"
const val pickRewardRoute = "$pickRewardBase/{$amountArg}"

fun buildPickRewardRoute(formattedAmount: String) =
  "$pickRewardBase/${Uri.encode(formattedAmount)}"

fun pickRewardScreen(
  navigateToEmail: (PaERewardType, String) -> Unit,
) = ScreenData.withAnalytics(
  route = pickRewardRoute,
  screenAnalyticsName = "PickReward",
  arguments = listOf(
    navArgument(amountArg) { type = NavType.StringType },
  ),
) { args, _, navigateBack ->
  val formattedAmount = args?.getString(amountArg)?.let { Uri.decode(it) }.orEmpty()
  val paeAnalytics = rememberPaEAnalytics()

  PickRewardScreen(
    formattedAmount = formattedAmount,
    navigateBack = navigateBack,
    onPick = { rewardType ->
      paeAnalytics.sendPaEPickRewardClick(rewardType)
      navigateToEmail(rewardType, formattedAmount)
    },
  )
}

@Composable
fun PickRewardScreen(
  formattedAmount: String,
  navigateBack: () -> Unit,
  onPick: (PaERewardType) -> Unit,
) {
  ExchangeFlowBackground {
    Column(modifier = Modifier.fillMaxSize()) {
      AppGamesTopBar(
        navigateBack = navigateBack,
        title = "",
        iconColor = Palette.White,
      )

      Spacer(modifier = Modifier.height(16.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
      ) {
        Text(
          text = stringResource(R.string.play_and_earn_exchange_pick_reward_title),
          style = AGTypography.Title,
          color = Palette.White,
        )
        Text(
          text = stringResource(
            R.string.play_and_earn_exchange_available_subtitle,
            formattedAmount
          ),
          style = AGTypography.Title,
          color = Palette.Yellow100,
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
          .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        PaERewardType.entries.forEach { variant ->
          RewardOptionCard(
            variant = variant,
            onClick = { onPick(variant) },
          )
        }
      }
    }
  }
}

@Composable
private fun RewardOptionCard(
  variant: PaERewardType,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(80.dp)
      .border(width = 1.dp, color = Palette.White)
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Image(
      painter = painterResource(variant.iconRes),
      contentDescription = null,
      modifier = Modifier.width(40.dp),
    )
    Text(
      modifier = Modifier.weight(1f),
      text = variant.exchangeDisplayName,
      style = AGTypography.SubHeadingM,
      color = Palette.White,
    )
    Image(
      imageVector = getForward(color1 = Palette.Secondary, color2 = Palette.White),
      contentDescription = null,
      modifier = Modifier.size(32.dp),
    )
  }
}

@Preview
@Composable
private fun PickRewardScreenPreview() {
  PickRewardScreen(
    formattedAmount = "$5.00",
    navigateBack = {},
    onPick = {},
  )
}
