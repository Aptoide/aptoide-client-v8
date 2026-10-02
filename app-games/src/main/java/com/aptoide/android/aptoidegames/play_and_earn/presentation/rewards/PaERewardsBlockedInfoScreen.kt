package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

/** Why a game's rewards are paused: its tracking tool hasn't confirmed the install yet. */
const val playAndEarnRewardsPausedInfoRoute = "playAndEarnRewardsPausedInfo"

/** Why this device isn't eligible for a game's campaign. */
const val playAndEarnNotEligibleInfoRoute = "playAndEarnNotEligibleInfo"

fun playAndEarnRewardsPausedInfoScreen() = ScreenData.withAnalytics(
  route = playAndEarnRewardsPausedInfoRoute,
  screenAnalyticsName = "PlayAndEarnRewardsPausedInfo",
) { _, _, _ ->
  PaERewardsBlockedInfo(
    title = stringResource(R.string.play_and_earn_rewards_paused_info_title),
    body = stringResource(R.string.play_and_earn_rewards_paused_info_body),
  )
}

fun playAndEarnNotEligibleInfoScreen() = ScreenData.withAnalytics(
  route = playAndEarnNotEligibleInfoRoute,
  screenAnalyticsName = "PlayAndEarnNotEligibleInfo",
) { _, _, _ ->
  PaERewardsBlockedInfo(
    title = stringResource(R.string.play_and_earn_not_eligible_info_title),
    body = stringResource(R.string.play_and_earn_not_eligible_info_body),
  )
}

@Composable
private fun PaERewardsBlockedInfo(
  title: String,
  body: String,
) {
  // No screen bar of its own (the app's top bar stays): the title below is
  // the heading, so it isn't repeated; back closes it.
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(text = title, style = AGTypography.Title, color = Palette.Yellow100)
    Text(text = body, style = AGTypography.SubHeadingS, color = Palette.White)
  }
}

@PreviewDark
@Composable
private fun PaERewardsBlockedInfoPreview() {
  AptoideTheme {
    PaERewardsBlockedInfo(
      title = stringResource(R.string.play_and_earn_rewards_paused_info_title),
      body = stringResource(R.string.play_and_earn_rewards_paused_info_body),
    )
  }
}
