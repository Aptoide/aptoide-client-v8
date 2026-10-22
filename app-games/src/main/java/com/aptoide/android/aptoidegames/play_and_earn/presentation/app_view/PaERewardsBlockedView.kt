package com.aptoide.android.aptoidegames.play_and_earn.presentation.app_view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.campaigns.domain.PaERewardsState
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaELargeTextButton
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.playAndEarnNotEligibleInfoRoute
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.playAndEarnRewardsPausedInfoRoute
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

/**
 * Replaces the missions on the game's Rewards tab while they don't count (AND-881): paused until
 * the game's tracking tool confirms the install, or not eligible after it rejected it.
 */
@Composable
fun PaERewardsBlockedView(
  state: PaERewardsState,
  navigate: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val notEligible = state == PaERewardsState.NOT_ELIGIBLE
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = stringResource(
        if (notEligible) R.string.play_and_earn_not_eligible_title
        else R.string.play_and_earn_rewards_paused_title
      ),
      style = AGTypography.Title,
      color = Palette.Yellow100,
      textAlign = TextAlign.Center,
    )
    Text(
      text = stringResource(
        if (notEligible) R.string.play_and_earn_not_eligible_body
        else R.string.play_and_earn_rewards_paused_body
      ),
      style = AGTypography.DescriptionGames,
      color = Palette.White,
      textAlign = TextAlign.Center,
    )
    PaELargeTextButton(
      title = stringResource(R.string.play_and_earn_rewards_blocked_learn_more_button),
      onClick = {
        navigate(
          if (notEligible) playAndEarnNotEligibleInfoRoute
          else playAndEarnRewardsPausedInfoRoute
        )
      },
      modifier = Modifier.fillMaxWidth(),
    )
  }
}

@PreviewDark
@Composable
private fun PaERewardsPausedPreview() {
  AptoideTheme {
    PaERewardsBlockedView(state = PaERewardsState.PAUSED, navigate = {})
  }
}

@PreviewDark
@Composable
private fun PaENotEligiblePreview() {
  AptoideTheme {
    PaERewardsBlockedView(state = PaERewardsState.NOT_ELIGIBLE, navigate = {})
  }
}
