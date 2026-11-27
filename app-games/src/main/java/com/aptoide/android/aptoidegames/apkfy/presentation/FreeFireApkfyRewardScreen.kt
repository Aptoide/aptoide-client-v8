package com.aptoide.android.aptoidegames.apkfy.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsAPKFY
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.mmp.WithUTM
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.PaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.restricted.PaERestrictedGate
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PAE_DEFAULT_REWARD_AMOUNT
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.RewardState
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.SignInRewardViewModel
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.rewardToAutoClaim
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.rewardToClaimOnTap
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in.rememberUserInfo
import com.aptoide.android.aptoidegames.theme.AptoideTheme

const val freeFireApkfyRewardRoute = "freeFireApkfyRewardRoute"

fun freeFireApkfyRewardScreen(
  navigateToSignIn: () -> Unit,
  navigateToHome: () -> Unit,
) = ScreenData.withAnalytics(
  route = freeFireApkfyRewardRoute,
  screenAnalyticsName = "FreeFireApkfyReward",
) { _, navigate, navigateBack ->
  // AND-951: a restricted account sees the restricted screen instead.
  // Nothing below runs meanwhile: the bonus is not claimed on the way back from sign-in.
  PaERestrictedGate(fullBleed = true) {
    val apkfyState = rememberApkfyState()
    val apkfyAnalytics = rememberApkfyAnalytics()
    val paeAnalytics = rememberPaEAnalytics()
    val viewModel = hiltViewModel<SignInRewardViewModel>()
    val userInfo = rememberUserInfo()
    val rewardState by viewModel.rewardState.collectAsState()
    // The reward amount comes from the backend FIRST_SIGN_IN mission (via SignInRewardRepository);
    // this screen only supplies the package-specific reward type.
    val rewardAmount = (rewardState as? RewardState.Unclaimed)?.reward?.rewardAmount
      ?: PAE_DEFAULT_REWARD_AMOUNT
    var awaitingSignIn by rememberSaveable { mutableStateOf(false) }

    // The reward is only claimed once the user returns from the sign-in flow actually signed in.
    // Navigation away is owned by the Claimed effect below, once the backend confirms the claim.
    // Keyed on the reward state too: a sign-in re-offers a refused bonus (AND-953), so it may
    // still be loading when the user is back. awaitingSignIn is reset first: one claim only.
    LaunchedEffect(userInfo, rewardState) {
      val reward = rewardToAutoClaim(awaitingSignIn, signedIn = userInfo != null, rewardState)
      if (reward != null) {
        awaitingSignIn = false
        viewModel.claim(reward.copy(paERewardType = PaERewardType.DIAMONDS))
        paeAnalytics.sendPaERewardClaimed(
          source = PaEAnalytics.SOURCE_APKFY,
          rewardType = PaERewardType.DIAMONDS,
          units = reward.units,
        )
      }
    }

    // A claimed reward means there is no offer to show: leave to home whether the claim just
    // happened here (the success dialog shows over home) or the user reached an already-claimed
    // offer (stale back stack entry, apkfy re-entry, or the mission state resolving late). A
    // refused one (AND-953) ends the offer the same way, without a dialog; the snackbar says why
    // over home.
    // A VPN refusal (AND-964) keeps the bonus Unclaimed, so the user stays here, sees the same
    // snackbar over this screen, and taps Collect again once the VPN is off.
    LaunchedEffect(rewardState) {
      if (rewardState is RewardState.Claimed || rewardState is RewardState.Refused) navigateToHome()
    }

    BackHandler {
      apkfyAnalytics.sendApkfyScreenBackClicked()
      navigateBack()
    }

    TransparentStatusBarEffect()

    apkfyState?.data?.let { apkfyData ->
      WithUTM(
        source = apkfyData.utmSource,
        medium = apkfyData.utmMedium,
        campaign = apkfyData.utmCampaign,
        content = apkfyData.utmContent,
        term = apkfyData.utmTerm,
        navigate = navigate,
      ) { navigateWithUtm ->
        OverrideAnalyticsAPKFY(navigateWithUtm) { navigateTo ->
          ApkfyRewardScreenContent(
            app = apkfyData.app,
            headerImage = R.drawable.free_fire_feature_graphic,
            rewardIcon = R.drawable.pae_ff_reward_icon,
            paERewardType = PaERewardType.DIAMONDS,
            rewardAmount = rewardAmount,
            navigateBack = {
              apkfyAnalytics.sendApkfyScreenBackClicked()
              navigateBack()
            },
            onAppClick = { navigateTo(buildAppViewRoute(apkfyData.app)) },
            onCollect = {
              if (rewardState is RewardState.Unclaimed) {
                paeAnalytics.sendPaERewardCardCollectClick(
                  source = PaEAnalytics.SOURCE_APKFY,
                  rewardType = PaERewardType.DIAMONDS,
                )
                // Signed in (back on the offer after a VPN refusal, AND-964): claim again here,
                // as the feed card does. Signed out: sign in first, auto-claimed on return.
                val reward = rewardToClaimOnTap(signedIn = userInfo != null, rewardState)
                if (reward != null) {
                  viewModel.claim(reward.copy(paERewardType = PaERewardType.DIAMONDS))
                  paeAnalytics.sendPaERewardClaimed(
                    source = PaEAnalytics.SOURCE_APKFY,
                    rewardType = PaERewardType.DIAMONDS,
                    units = reward.units,
                  )
                } else {
                  awaitingSignIn = true
                  navigateToSignIn()
                }
              }
            },
            onDismiss = {
              paeAnalytics.sendPaERewardCardDismissClick(
                source = PaEAnalytics.SOURCE_APKFY,
                rewardType = PaERewardType.DIAMONDS,
              )
              navigateBack()
            },
          )
        }
      }
    } ?: GenericErrorView(navigateBack)
  }
}

@PreviewDark
@Composable
private fun FreeFireApkfyRewardScreenPreview() {
  AptoideTheme {
    ApkfyRewardScreenContent(
      app = randomApp,
      headerImage = R.drawable.free_fire_feature_graphic,
      rewardIcon = R.drawable.pae_ff_reward_icon,
      paERewardType = PaERewardType.DIAMONDS,
      rewardAmount = PAE_DEFAULT_REWARD_AMOUNT,
      navigateBack = {},
      onAppClick = {},
      onCollect = {},
      onDismiss = {},
    )
  }
}
