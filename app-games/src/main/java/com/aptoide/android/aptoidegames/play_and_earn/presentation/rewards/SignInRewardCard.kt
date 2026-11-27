package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.PaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.restricted.rememberPaERestrictedGateState
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in.playAndEarnRewardSignInRoute
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in.rememberUserInfo

@Composable
fun SignInRewardCard(
  packageName: String?,
  navigate: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val rewardType = PaERewardType.fromPackageName(packageName) ?: return
  val viewModel = hiltViewModel<SignInRewardViewModel>()
  val paeAnalytics = rememberPaEAnalytics()
  val rewardState by viewModel.rewardState.collectAsState()
  val userInfo = rememberUserInfo()
  var awaitingSignIn by rememberSaveable { mutableStateOf(false) }

  // Retry a failed startup mission fetch, so a Loading state doesn't hide the offer all session.
  LaunchedEffect(Unit) {
    viewModel.refresh()
  }

  // After the user returns from the reward sign-in flow signed in, claim automatically so the
  // success dialog shows (mirrors the apkfy reward flow).
  // Keyed on the reward state too: a sign-in re-offers a refused bonus (AND-953), so it may
  // still be loading when the user is back. awaitingSignIn is reset first: one claim only.
  LaunchedEffect(userInfo, rewardState) {
    val reward = rewardToAutoClaim(awaitingSignIn, signedIn = userInfo != null, rewardState)
    if (reward != null) {
      awaitingSignIn = false
      viewModel.claim(reward.copy(paERewardType = rewardType))
      paeAnalytics.sendPaERewardClaimed(
        source = PaEAnalytics.SOURCE_GAMES_FEED,
        rewardType = rewardType,
        units = reward.units,
      )
    }
  }

  // AND-951: a restricted account is not offered the bonus.
  val restriction = rememberPaERestrictedGateState()
  val unclaimed = signInBonusOffer(rewardState, restriction) ?: return

  LaunchedEffect(rewardType) {
    viewModel.onRewardCardShown(rewardType) {
      paeAnalytics.sendPaERewardCardShown(
        source = PaEAnalytics.SOURCE_GAMES_FEED,
        rewardType = rewardType,
      )
    }
  }

  ClaimableRewardCard(
    rewardType = rewardType,
    formattedAmount = unclaimed.reward.rewardAmount,
    // Signed in: claim directly. Signed out: sign in first, then auto-claim on return (above).
    onCollectClick = {
      paeAnalytics.sendPaERewardCardCollectClick(
        source = PaEAnalytics.SOURCE_GAMES_FEED,
        rewardType = rewardType,
      )
      if (userInfo != null) {
        viewModel.claim(unclaimed.reward.copy(paERewardType = rewardType))
        paeAnalytics.sendPaERewardClaimed(
          source = PaEAnalytics.SOURCE_GAMES_FEED,
          rewardType = rewardType,
          units = unclaimed.reward.units,
        )
      } else {
        awaitingSignIn = true
        navigate(playAndEarnRewardSignInRoute)
      }
    },
    modifier = modifier,
  )
}
